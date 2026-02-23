package com.example.commerce.customer.dto;

import java.util.List;

public record GetCustomersResponse(
        List<GetOneCustomerResponse> customers
) {}
