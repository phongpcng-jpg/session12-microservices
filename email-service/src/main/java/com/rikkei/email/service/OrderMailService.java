package com.rikkei.email.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.rikkei.common.event.OrderCreatedEvent;
import com.rikkei.email.config.MailProperties;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderMailService {

    private final JavaMailSender mailSender;
    private final MailProperties properties;

    public void sendOrderConfirmation(OrderCreatedEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(properties.from());
        message.setTo(event.customerEmail());
        message.setSubject("Xác nhận đơn hàng #" + event.orderId());
        message.setText("""
                Cảm ơn bạn đã đặt hàng.

                Mã đơn hàng: %d
                Sản phẩm: %d
                Số lượng: %d
                Tổng tiền: %s
                """.formatted(event.orderId(), event.productId(), event.quantity(), event.totalAmount().toPlainString()));
        mailSender.send(message);
    }
}
