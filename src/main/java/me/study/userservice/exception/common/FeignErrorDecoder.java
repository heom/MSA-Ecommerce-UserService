package me.study.userservice.exception.common;

import feign.Response;
import feign.codec.ErrorDecoder;
import me.study.userservice.exception.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()){
            case 404:
                if(methodKey.contains("getOrders")){
                    return new FeignException(methodKey, "API Not Found", HttpStatus.valueOf(response.status()));
                }
            default:
                return new FeignException(methodKey, "Server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
