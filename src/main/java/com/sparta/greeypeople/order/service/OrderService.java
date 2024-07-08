package com.sparta.greeypeople.order.service;

import com.sparta.greeypeople.exception.DataNotFoundException;
import com.sparta.greeypeople.exception.ForbiddenException;
import com.sparta.greeypeople.menu.entity.Menu;
import com.sparta.greeypeople.menu.repository.MenuRepository;
import com.sparta.greeypeople.order.dto.request.OrderMenuList;
import com.sparta.greeypeople.order.dto.request.OrderRequestDto;
import com.sparta.greeypeople.order.dto.response.OrderResponseDto;
import com.sparta.greeypeople.order.entity.Order;
import com.sparta.greeypeople.order.entity.OrderMenu;
import com.sparta.greeypeople.order.enumeration.Process;
import com.sparta.greeypeople.order.repository.OrderRepository;
import com.sparta.greeypeople.store.entity.Store;
import com.sparta.greeypeople.store.repository.StoreRepository;
import com.sparta.greeypeople.user.entity.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;

    @Transactional
    public OrderResponseDto createOrder(Long storeId, OrderRequestDto orderRequest, User user) {
        Store store = findStore(storeId);
        Process process = Process.COMPLETED;

        List<Long> ids = orderRequest.getMenuList().stream()
            .map(OrderMenuList::getMenuId)
            .toList();

        List<Menu> menus = menuRepository.findAllById(ids);

        Order order = new Order(store, user, process);

        List<OrderMenu> orderMenus = menus.stream().map(OrderMenu::new).toList();
        orderMenus.forEach(order::addOrderMenu);

        Order savedOrder = orderRepository.save(order);

        if (!order.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("주문 작성 권한이 없습니다.");
        }
        return new OrderResponseDto(savedOrder);
    }

    public OrderResponseDto getOrder(Long orderId) {
        Order order = findOrder(orderId);

        return new OrderResponseDto(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getAllOrder(Pageable pageable) {
        Page<Order> orderList = orderRepository.findAll(pageable);
        return orderList.map(order -> new OrderResponseDto(order));
    }

    @Transactional
    public OrderResponseDto updateOrdeer(Long orderId, OrderRequestDto orderRequest, User user) {
        Order order = findOrder(orderId);

        if (!order.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("수정 권한이 없습니다.");
        }

        List<Long> ids = orderRequest.getMenuList().stream()
            .map(OrderMenuList::getMenuId)
            .collect(Collectors.toList());

        List<Menu> menus = menuRepository.findAllById(ids);

        List<OrderMenu> orderMenus = menus.stream()
            .map(OrderMenu::new)
            .collect(Collectors.toList());
        order.update(orderMenus);

        Order savedOrder = orderRepository.save(order);
        return new OrderResponseDto(savedOrder);
    }

    public void deleteOrder(Long orderId, User user) {
        Order order = findOrder(orderId);
        if (!order.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("삭제 권한이 없습니다.");
        }
//        Process process = Process.CANCELED;
        orderRepository.delete(order);
    }

    public Store findStore(Long storeId) {
        return storeRepository.findById(storeId).orElseThrow(
            () -> new DataNotFoundException("조회된 가게의 정보가 없습니다.")
        );
    }

    public Order findOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(
            () -> new DataNotFoundException("조회된 주문 정보가 없습니다")
        );
    }

}

