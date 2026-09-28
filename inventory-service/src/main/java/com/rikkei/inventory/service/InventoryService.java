package com.rikkei.inventory.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rikkei.inventory.domain.Reservation;
import com.rikkei.inventory.domain.ReservationStatus;
import com.rikkei.inventory.domain.Stock;
import com.rikkei.inventory.dto.ReservationResponse;
import com.rikkei.inventory.dto.ReserveRequest;
import com.rikkei.inventory.dto.StockResponse;
import com.rikkei.inventory.exception.OutOfStockException;
import com.rikkei.inventory.exception.ReservationNotFoundException;
import com.rikkei.inventory.exception.StockNotFoundException;
import com.rikkei.inventory.mapper.InventoryMapper;
import com.rikkei.inventory.repository.ReservationRepository;
import com.rikkei.inventory.repository.StockRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final StockRepository stockRepository;
    private final ReservationRepository reservationRepository;
    private final ProductLockExecutor lockExecutor;
    private final InventoryMapper mapper;

    public ReservationResponse reserve(ReserveRequest request) {
        return lockExecutor.execute(request.productId(), () -> doReserve(request));
    }

    public ReservationResponse release(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));
        return lockExecutor.execute(reservation.getProductId(), () -> doRelease(reservationId));
    }

    public StockResponse updateQuantity(Long productId, int quantity) {
        return lockExecutor.execute(productId, () -> doUpdateQuantity(productId, quantity));
    }

    @Transactional(readOnly = true)
    public StockResponse getStock(Long productId) {
        return stockRepository.findByProductId(productId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new StockNotFoundException(productId));
    }

    @Transactional
    public void confirm(UUID reservationId) {
        reservationRepository.findById(reservationId).ifPresent(reservation -> {
            if (reservation.getStatus() == ReservationStatus.RESERVED) {
                reservation.setStatus(ReservationStatus.CONFIRMED);
                reservationRepository.save(reservation);
            }
        });
    }

    private ReservationResponse doReserve(ReserveRequest request) {
        return reservationRepository.findById(request.reservationId())
                .map(mapper::toResponse)
                .orElseGet(() -> {
                    Stock stock = stockRepository.findByProductId(request.productId())
                            .orElseThrow(() -> new StockNotFoundException(request.productId()));
                    if (stock.getQuantity() < request.quantity()) {
                        throw new OutOfStockException();
                    }
                    stock.setQuantity(stock.getQuantity() - request.quantity());
                    stockRepository.saveAndFlush(stock);

                    Reservation reservation = new Reservation();
                    reservation.setId(request.reservationId());
                    reservation.setProductId(request.productId());
                    reservation.setQuantity(request.quantity());
                    reservation.setStatus(ReservationStatus.RESERVED);
                    return mapper.toResponse(reservationRepository.saveAndFlush(reservation));
                });
    }

    private ReservationResponse doRelease(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));
        if (reservation.getStatus() == ReservationStatus.RESERVED) {
            Stock stock = stockRepository.findByProductId(reservation.getProductId())
                    .orElseThrow(() -> new StockNotFoundException(reservation.getProductId()));
            stock.setQuantity(stock.getQuantity() + reservation.getQuantity());
            stockRepository.saveAndFlush(stock);
            reservation.setStatus(ReservationStatus.RELEASED);
            reservationRepository.saveAndFlush(reservation);
        }
        return mapper.toResponse(reservation);
    }

    private StockResponse doUpdateQuantity(Long productId, int quantity) {
        Stock stock = stockRepository.findByProductId(productId).orElseGet(() -> {
            Stock created = new Stock();
            created.setProductId(productId);
            return created;
        });
        stock.setQuantity(quantity);
        return mapper.toResponse(stockRepository.saveAndFlush(stock));
    }
}
