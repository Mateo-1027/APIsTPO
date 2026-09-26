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
    private ProductRepository productRepository;

    @Override
    @Transactional
    public CartResponse getCart(User user) {
        Cart cart = getRawCart(user);
        cart.getItems().forEach(item -> item.setSubtotal(calculateSubtotal(item.getProduct(), item.getQuantity())));
        recalculateTotal(cart);
        return mapToResponse(cart);
    }

    @Override
    public Cart getRawCart(User user) {
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
    public CartResponse addItemToCart(User user, CartItemRequest request) {
        Cart cart = getRawCart(user);
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        Product product = productRepository.findByIdAndActiveTrue(request.getProductId())
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
    public CartResponse updateItemQuantity(User user, CartItemRequest request) {
        Cart cart = getRawCart(user);

    // 1. Buscamos el ítem usando el ID de producto que viene en la request
    ItemCart item = cart.getItems().stream()
            .filter(i -> i.getProduct().getId().equals(request.getProductId()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("El artículo no se encuentra en el carrito"));

    int newQuantity = request.getQuantity();

    // 2. Si la cantidad enviada es <= 0, delegamos a la eliminación del ítem
    if (newQuantity <= 0) {
        return removeItemFromCart(user, request);
    }

    // 3. Validamos stock disponible
    if (item.getProduct().getStock() < newQuantity) {
        throw new InsufficientStockException("No hay suficiente stock para el producto: " + item.getProduct().getName());
    }

    // 4. Actualizamos cantidad y subtotal
    item.setQuantity(newQuantity);
    item.setSubtotal(calculateSubtotal(item.getProduct(), newQuantity));

    // 5. Recalculamos total global del carrito y guardamos
    recalculateTotal(cart);
    return mapToResponse(cartRepository.save(cart));
}

    @Override
    @Transactional
    public CartResponse removeItemFromCart(User user,  CartItemRequest request) {
        Cart cart = getRawCart(user);

    // Elimina de la colección el ítem cuyo productId coincida con el del request
    boolean removed = cart.getItems().removeIf(item -> 
        item.getProduct().getId().equals(request.getProductId())
    );

    if (!removed) {
        throw new ProductNotFoundException("El producto no existía en el carrito");
    }

    recalculateTotal(cart);
    return mapToResponse(cartRepository.save(cart));

}

    @Override
    @Transactional
    public void clearCart(User user) {
        Cart cart = getRawCart(user);
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
        response.setEmail(cart.getUser().getEmail());
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
        if (items.isEmpty()) {
            response.setMessage("Tu carrito está vacío.");
        }
        return response;
    }
}






