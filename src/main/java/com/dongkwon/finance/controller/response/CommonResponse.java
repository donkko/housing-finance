package com.dongkwon.finance.controller.response;

import lombok.Value;

@Value(staticConstructor = "with")
public class CommonResponse <T> {
    T result;
}
