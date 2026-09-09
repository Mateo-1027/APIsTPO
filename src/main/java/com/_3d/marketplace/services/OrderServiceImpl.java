package com._3d.marketplace.services;

import com._3d.marketplace.entity.Cart;
import com._3d.marketplace.entity.ItemCart;
import com._3d.marketplace.entity.Order;
import com._3d.marketplace.entity.OrderItem;
import com._3d.marketplace.entity.Product;
import com._3d.marketplace.entity.Role;
import com._3d.marketplace.entity.User;
import com._3d.marketplace.entity.dto.OrderItemResponse;
import com._3d.marketplace.entity.dto.OrderResponse;
import com._3d.marketplace.exceptions.ForbiddenOperationException;
import com._3d.marketplace.exceptions.EmptyCartException;
import com._3d.marketplace.exceptions.InsufficientStockException;
import com._3d.marketplace.exceptions.ProductNotFoundException;
import com._3d.marketplace.repositories.OrderRepository;
import com._3d.marketplace.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    public OrderResponse checkout(User user) {
        Cart cart = cartService.getRawCart(user);

        if (cart.getItems().isEmpty()) {
            throw new EmptyCartException("El carrito está vacío");
        }

        Order order = new Order();
        order.setUser(user);
        order.setDate(LocalDateTime.now());
        order.setTotal(0.0);

        double total = 0.0;

        for (ItemCart item : cart.getItems()) {
            Product product = item.getProduct();
            if (!product.isActive()) {
                throw new ProductNotFoundException("El producto ya no está disponible: " + product.getName());
            }
            if (product.getStock() < item.getQuantity()) {
                throw new InsufficientStockException("No hay suficiente stock para el producto: " + product.getName());
            }

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

        cartService.clearCart(user);

        return mapToResponse(savedOrder);
    }

    @Override
    @Transactional
    public List<OrderResponse> getHistory(User user) {
        return orderRepository.findByUserOrderByDateDesc(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<OrderResponse> getOrderById(Long orderId, User user) {
        Optional<Order> order = orderRepository.findById(orderId);
        order.ifPresent(found -> checkAccess(found, user));
        return order.map(this::mapToResponse);
    }

    private void checkAccess(Order order, User user) {
        boolean isAdmin = user.getRoles().contains(Role.ADMIN);
        if (!isAdmin && !order.getUser().getId().equals(user.getId())) {
            throw new ForbiddenOperationException("No tenés permiso para ver una orden que no es tuya.");
        }
    }

    @Override
    @Transactional
    public List<OrderResponse> getSales(User seller) {
        return orderRepository.findDistinctByItems_Product_Seller_IdOrderByDateDesc(seller.getId())
                .stream()
                .map(order -> mapToSellerResponse(order, seller.getId()))
                .collect(Collectors.toList());
    }

    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setDate(order.getDate());
        response.setTotal(order.getTotal());
        response.setEmail(order.getUser().getEmail());
        response.setItems(order.getItems().stream().map(this::mapItem).collect(Collectors.toList()));
        return response;
    }

    private OrderResponse mapToSellerResponse(Order order, Long sellerId) {
        List<OrderItem> ownItems = order.getItems().stream()
                .filter(item -> item.getProduct().getSeller() != null
                        && item.getProduct().getSeller().getId().equals(sellerId))
                .collect(Collectors.toList());

        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setDate(order.getDate());
        response.setEmail(order.getUser().getEmail());
        response.setItems(ownItems.stream().map(this::mapItem).collect(Collectors.toList()));
        response.setTotal(ownItems.stream().mapToDouble(OrderItem::getSubtotal).sum());
        return response;
    }

    private OrderItemResponse mapItem(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        response.setProductId(item.getProduct().getId());
        response.setProductName(item.getProduct().getName());
        response.setQuantity(item.getQuantity());
        response.setUnitPrice(item.getUnitPrice());
        response.setSubtotal(item.getSubtotal());
        return response;
    }}
