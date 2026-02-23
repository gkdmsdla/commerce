package com.example.commerce.customer.dto;

import lombok.RequiredArgsConstructor;

import lombok.Getter;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class GetCustomersResponse {

    private final List<GetOneCustomerResponse> customers;

}
