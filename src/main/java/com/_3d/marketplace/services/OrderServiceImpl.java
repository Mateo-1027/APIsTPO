package com._3d.marketplace.services;

import com._3d.marketplace.entity.Cart;
import com._3d.marketplace.entity.ItemCart;
import com._3d.marketplace.entity.Order;
import com._3d.marketplace.entity.OrderItem;
import com._3d.marketplace.entity.Product;
import com._3d.marketplace.entity.User;
import com._3d.marketplace.entity.dto.OrderResponse;
import com._3d.marketplace.exceptions.InsufficientStockException;
import com._3d.marketplace.repositories.OrderRepository;
import com._3d.marketplace.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public OrderResponse checkout(String username) {
        User user = userService.findByUsername(username);
        Cart cart = cartService.getRawCartByUsername(username);

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        Order order = new Order();
        order.setUser(user);
        order.setDate(LocalDateTime.now());
        order.setTotal(0.0);

        double total = 0.0;

        for (ItemCart item : cart.getItems()) {
            Product product = item.getProduct();
            if (product.getStock() < item.getQuantity()) {
                throw new InsufficientStockException("No hay suficiente stock para el producto: " + product.getName());
            }

            // Deduct stock
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            
            Double effectivePrice = product.getPrice() * (1 - (product.getDiscount() != null ? product.getDiscount() : 0.0) / 100);
            orderItem.setUnitPrice(effectivePrice);
            orderItem.setSubtotal(effectivePrice * item.getQuantity());
            
            order.getItems().add(orderItem);
            total += orderItem.getSubtotal();
        }

        order.setTotal(total);
        Order savedOrder = orderRepository.save(order);
        
        cartService.clearCart(username);

        return mapToResponse(savedOrder);
    }

    @Override
    public List<OrderResponse> getHistory(String username) {
        User user = userService.findByUsername(username);
        return orderRepository.findByUserOrderByDateDesc(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setDate(order.getDate());
        response.setTotal(order.getTotal());
        response.setUsername(order.getUser().getUsername());
        return response;
    }
}
