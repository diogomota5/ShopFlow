package com.shopflow.backend.service;

import com.shopflow.backend.entity.User;
import com.shopflow.backend.exception.BusinessRuleException;
import com.shopflow.backend.entity.Order;
import com.shopflow.backend.entity.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    @Value("${spring.mail.username:}")
    private String sender;

    public void sendVerificationEmail(User user) {
        send(user, "Confirme seu cadastro na ShopFlow", "Olá, " + user.getName() + "!\n\n"
                + "Clique no link abaixo para confirmar seu e-mail e ativar sua conta:\n"
                + frontendUrl + "/verify-email?token=" + user.getEmailVerificationToken() + "\n\n"
                + "Este link é válido por 24 horas.\n\nEquipe ShopFlow");
    }

    public void sendPasswordResetEmail(User user) {
        send(user, "Redefina sua senha na ShopFlow", "Olá, " + user.getName() + "!\n\n"
                + "Use o link abaixo para criar uma nova senha:\n"
                + frontendUrl + "/reset-password?token=" + user.getPasswordResetToken() + "\n\n"
                + "Este link é válido por 30 minutos e só pode ser usado uma vez.\n\nEquipe ShopFlow");
    }

    public void sendOrderStatusEmail(Order order, OrderStatus status) {
        if (sender.isBlank()) return;
        String subject = "Atualização do pedido " + order.getId().toString().substring(0, 8).toUpperCase();
        String text = "Olá, " + order.getUser().getName() + "!\n\n"
                + "Seu pedido " + order.getId().toString().substring(0, 8).toUpperCase()
                + " foi atualizado para: " + statusLabel(status) + ".\n\n"
                + "Acompanhe seus pedidos na sua conta ShopFlow.\n\nEquipe ShopFlow";
        try {
            sendMessage(order.getUser().getEmail(), subject, text);
        } catch (MailException | BusinessRuleException ignored) {
            // Notificações não devem desfazer uma operação de pedido já persistida.
        }
    }

    private void send(User user, String subject, String text) {
        if (sender.isBlank()) {
            throw new BusinessRuleException("O serviço de e-mail não está configurado. Tente novamente mais tarde.");
        }

        sendMessage(user.getEmail(), subject, text);
    }

    private void sendMessage(String recipient, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(text);
        try {
            mailSender.send(message);
        } catch (MailException ex) {
            throw new BusinessRuleException("Não foi possível enviar o e-mail. Tente novamente mais tarde.");
        }
    }

    private String statusLabel(OrderStatus status) {
        return switch (status) {
            case PENDING -> "Pendente";
            case PAID -> "Pago";
            case SHIPPED -> "Enviado";
            case DELIVERED -> "Entregue";
            case CANCELED -> "Cancelado";
        };
    }
}
