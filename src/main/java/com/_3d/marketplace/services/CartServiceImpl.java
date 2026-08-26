package com._3d.marketplace.services;

import com._3d.marketplace.entity.Cart;
import com._3d.marketplace.entity.ItemCart;
import com._3d.marketplace.entity.Product;
import com._3d.marketplace.entity.User;
import com._3d.marketplace.entity.dto.CartItemRequest;
import com._3d.marketplace.entity.dto.CartItemResponse;
import com._3d.marketplace.entity.dto.CartResponse;
import com._3d.marketplace.exceptions.CartNotFoundException;
import com._3d.marketplace.exceptions.InsufficientStockException;
import com._3d.marketplace.exceptions.ProductNotFoundException;
import com._3d.marketplace.repositories.CartRepository;
import com._3d.marketplace.repositories.ItemCartRepository;
import com._3d.marketplace.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemCartRepository itemCartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserService userService;

    @Override
    public CartResponse getCartByUsername(String username) {
        return mapToResponse(getRawCartByUsername(username));
    }

    @Override
    public Cart getRawCartByUsername(String username) {
        User user = userService.findByUsername(username);
        return cartRepository.findByUser(user)
                .orElseGet(() -> createCartForUser(user));
    }

    private Cart createCartForUser(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setTotal(0.0);
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public CartResponse addItemToCart(String username, CartItemRequest request) {
        Cart cart = getRawCartByUsername(username);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("El producto no existe"));

        if (product.getStock() < request.getQuantity()) {
            throw new InsufficientStockException("No hay suficiente stock para el producto: " + product.getName());
        }

        Optional<ItemCart> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            ItemCart item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();
            if (product.getStock() < newQuantity) {
                throw new InsufficientStockException("No hay suficiente stock para el producto: " + product.getName());
            }
            item.setQuantity(newQuantity);
            item.setSubtotal(calculateSubtotal(product, item.getQuantity()));
        } else {
            ItemCart newItem = new ItemCart();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(request.getQuantity());
            newItem.setSubtotal(calculateSubtotal(product, request.getQuantity()));
            cart.getItems().add(newItem);
        }

        recalculateTotal(cart);
        return mapToResponse(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public CartResponse updateItemQuantity(String username, Long itemId, Integer quantity) {
        Cart cart = getRawCartByUsername(username);
        ItemCart item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("El artículo no se encuentra en el carrito"));

        if (quantity <= 0) {
            return removeItemFromCart(username, itemId);
        }

        if (item.getProduct().getStock() < quantity) {
            throw new InsufficientStockException("No hay suficiente stock para el producto: " + item.getProduct().getName());
        }

        item.setQuantity(quantity);
        item.setSubtotal(calculateSubtotal(item.getProduct(), quantity));
        recalculateTotal(cart);
        return mapToResponse(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public CartResponse removeItemFromCart(String username, Long itemId) {
        Cart cart = getRawCartByUsername(username);
        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        recalculateTotal(cart);
        return mapToResponse(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public void clearCart(String username) {
        Cart cart = getRawCartByUsername(username);
        cart.getItems().clear();
        cart.setTotal(0.0);
        cartRepository.save(cart);
    }

    private Double calculateSubtotal(Product product, Integer quantity) {
        Double effectivePrice = product.getPrice() * (1 - (product.getDiscount() != null ? product.getDiscount() : 0.0) / 100);
        return effectivePrice * quantity;
    }

    private void recalculateTotal(Cart cart) {
        Double total = cart.getItems().stream()
                .mapToDouble(ItemCart::getSubtotal)
                .sum();
        cart.setTotal(total);
    }

    private CartResponse mapToResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setUsername(cart.getUser().getUsername());
        response.setTotal(cart.getTotal());

        List<CartItemResponse> items = cart.getItems().stream().map(item -> {
            CartItemResponse itemResponse = new CartItemResponse();
            itemResponse.setId(item.getId());
            itemResponse.setProductId(item.getProduct().getId());
            itemResponse.setProductName(item.getProduct().getName());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setSubtotal(item.getSubtotal());
            return itemResponse;
        }).collect(Collectors.toList());

        response.setItems(items);
        return response;
    }
}
