package com.example.commerce.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetCustomersResponse {

    private final List<GetOneCustomerResponse> customers;

}
