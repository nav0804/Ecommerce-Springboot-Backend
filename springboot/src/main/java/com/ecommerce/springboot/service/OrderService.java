package com.ecommerce.springboot.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.ecommerce.springboot.exception.LoginException;
import com.ecommerce.springboot.exception.OrderException;
import com.ecommerce.springboot.models.CartDto;
import com.ecommerce.springboot.models.CartItem;
import com.ecommerce.springboot.models.Customer;
import com.ecommerce.springboot.models.Order;
import com.ecommerce.springboot.models.OrderDto;
import com.ecommerce.springboot.models.OrderStatusValues;
import com.ecommerce.springboot.models.ProductStatus;
import com.ecommerce.springboot.repository.OrderRepository;

public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CartService cartService;

    public Order saveOrder(OrderDto orderDto, String token) throws LoginException, OrderException {

        Order order = new Order();

        Customer loggedInCustomer = customerService.getLoggedInCustomerDetails(token);
        if (loggedInCustomer != null) {
            order.setCustomer(loggedInCustomer);
            String usersCardNumber = loggedInCustomer.getCreditCard().getCardNumber();
            String userGivenCardNumber = orderDto.getCardNumber().getCardNumber();
            List<CartItem> productsInCart = loggedInCustomer.getCustomerCart().getCartItems();
            List<CartItem> productsInOrder = new ArrayList<>(productsInCart);
            order.setOrderCartItems(productsInOrder);
            order.setTotal(loggedInCustomer.getCustomerCart().getCartTotal());

            if (productsInCart.size() != 0) {
                if ((usersCardNumber.equals(userGivenCardNumber)) && (orderDto.getCardNumber().getCardValidity()
                        .equals(loggedInCustomer.getCreditCard().getCardValidity())
                        && (orderDto.getCardNumber().getCardCVV()
                                .equals(loggedInCustomer.getCreditCard().getCardCVV())))) {
                    order.setCardNumber(orderDto.getCardNumber().getCardNumber());
                    order.setAddress(loggedInCustomer.getAddress().get(orderDto.getAddressType()));
                    order.setDate(LocalDate.now());
                    order.setOrderStatus(OrderStatusValues.SUCCESS);
                    List<CartItem> cartItems = loggedInCustomer.getCustomerCart().getCartItems();
                    for (CartItem cartItem : cartItems) {
                        Integer remainingQuantity = cartItem.getCartProduct().getQuantity()
                                - cartItem.getCartItemQuantity();
                        if (remainingQuantity < 0
                                || cartItem.getCartProduct().getStatus() == ProductStatus.OUTOFSTOCK) {
                            CartDto cartDto = new CartDto();
                            cartDto.setProductId(cartItem.getCartProduct().getProductId());
                            cartService.removeProductFromCart(cartDto, token);
                            throw new OrderException(
                                    "Product" + cartItem.getCartProduct().getProductName() + "Out of stock");
                        }
                        cartItem.getCartProduct().setQuantity(remainingQuantity);
                        if (cartItem.getCartProduct().getQuantity() == 0) {
                            cartItem.getCartProduct().setStatus(ProductStatus.OUTOFSTOCK);
                        }
                    }
                    cartService.clearCart(token);
                    return orderRepository.save(order);

                } else {
                    order.setCardNumber(null);
                    order.setAddress(loggedInCustomer.getAddress().get(orderDto.getAddressType()));
                    order.setDate(LocalDate.now());
                    order.setOrderStatus(OrderStatusValues.PENDING);
                    cartService.clearCart(token);
                    return orderRepository.save(order);
                }
            } else {
                throw new OrderException("No products in cart");
            }
        } else {
            throw new LoginException("Invalid session token");
        }
    }

    public Order getOrderByOrderId(Integer OrderId) throws OrderException {
        return orderRepository.findById(OrderId).orElseThrow(() -> new OrderException("Order not found"));
    }

    public List<Order> getAllOrders() throws OrderException {
        List<Order> allOrders = (List<Order>) orderRepository.findAll();
        if (allOrders.size() > 0)
            return allOrders;
        throw new OrderException("No orders in your account");

    }

    public Order cancelOrderByOrderId(Integer OrderId, String token) throws OrderException {
        Order order = orderRepository.findById(OrderId)
                .orElseThrow(() -> new OrderException("No order exists with the given order id" + OrderId));
        if (order.getCustomer().getCustomerId() == customerService.getLoggedInCustomerDetails(token).getCustomerId()) {
            if (order.getOrderStatus() == OrderStatusValues.PENDING) {
                order.setOrderStatus(OrderStatusValues.CANCELLED);
                orderRepository.save(order);
                return order;
            } else if (order.getOrderStatus() == OrderStatusValues.SUCCESS) {
                order.setOrderStatus(OrderStatusValues.CANCELLED);
                List<CartItem> cartItems = order.getOrderCartItems();
                for (CartItem cartItem : cartItems) {
                    Integer addedQuantity = cartItem.getCartProduct().getQuantity() + cartItem.getCartItemQuantity();
                    cartItem.getCartProduct().setQuantity(addedQuantity);
                    if (cartItem.getCartProduct().getStatus() == ProductStatus.OUTOFSTOCK) {
                        cartItem.getCartProduct().setStatus(ProductStatus.AVAILABLE);
                    }
                    orderRepository.save(order);
                    return order;
                }
            } else {
                throw new OrderException("Order was already cancelled");
            }
        } else {
            throw new OrderException("Invalid session");
        }
    }

    public Order updateOrderByOrderId(OrderDto orderDto, Integer OrderId, String token)
            throws OrderException, LoginException {

        Order existingOrder = orderRepository.findById(OrderId)
                .orElseThrow(() -> new OrderException("No orders exist"));

        if (existingOrder.getCustomer().getCustomerId() == customerService.getLoggedInCustomerDetails(token)
                .getCustomerId()) {
            Customer loggedInCustomer =
            customerService.getLoggedInCustomerDetails(token);
            String userCardNumber = loggedInCustomer.getCreditCard().getCardNumber();
            String userGivenCardNumber = orderDto.getCardNumber().getCardNumber();
            if ((userCardNumber.equals(userGivenCardNumber))
                    && (orderDto.getCardNumber().getCardValidity()
                            .equals(loggedInCustomer.getCreditCard().getCardValidity())
                            && (orderDto.getCardNumber().getCardCVV()
                                    .equals(loggedInCustomer.getCreditCard().getCardCVV())))) {
                existingOrder.setCardNumber(orderDto.getCardNumber().getCardNumber());
                existingOrder.setAddress(existingOrder.getCustomer().getAddress().get(orderDto.getAddressType()));
                existingOrder.setOrderStatus(OrderStatusValues.SUCCESS);
                List<CartItem> cartItems = existingOrder.getOrderCartItems();
                for (CartItem cartItem : cartItems) {
                    Integer remainingQuantity = cartItem.getCartProduct().getQuantity()
                            - cartItem.getCartItemQuantity();
                    if (remainingQuantity < 0 || cartItem.getCartProduct().getStatus() == ProductStatus.OUTOFSTOCK) {
                        CartDto cartDto = new CartDto();
                        cartDto.setProductId(cartItem.getCartProduct().getProductId());
                        cartService.removeProductFromCart(cartDto, token);
                        throw new OrderException(
                                "Product" + cartItem.getCartProduct().getProductName() + "Out of stock");
                    }
                    cartItem.getCartProduct().setQuantity(remainingQuantity);
                    if (cartItem.getCartProduct().getQuantity() == 0) {
                        cartItem.getCartProduct().setStatus(ProductStatus.OUTOFSTOCK);
                    }
                }
                return orderRepository.save(existingOrder);
            } else {
                throw new OrderException("Incorrect card number");
            }
        } else {
            throw new LoginException("Invalid session");
        }

    }

    public List<Order> getAllOrdersByDate(LocalDate date) throws OrderException {
        List<Order> orders = orderRepository.findByDate(date);
        return orders;

    }

    public Customer getCustomerByOrderId(Integer orderId) throws OrderException {
        Optional<Order> opt = orderRepository.findById(orderId);
        if (opt.isPresent()) {
            Order exisOrder = opt.get();
            return orderRepository.getCustomerByOrderId(exisOrder.getCustomer().getCustomerId());
        } else {
            throw new OrderException("No order exists with given orderid" + orderId);
        }
    }

}
