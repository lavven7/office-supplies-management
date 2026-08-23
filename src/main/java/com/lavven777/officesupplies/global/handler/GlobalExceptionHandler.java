package com.lavven777.officesupplies.global.handler;

import com.lavven777.officesupplies.global.exception.BusinessException;
import com.lavven777.officesupplies.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Controller
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public String handleBusinessException(BusinessException e, Model model, HttpServletResponse response) {
        ErrorCode errorCode = e.getErrorCode();
        response.setStatus(errorCode.getStatus());

        model.addAttribute("errorCode", errorCode.getStatus());
        model.addAttribute("errorMessage", errorCode.getMessage());

        return "error/business-error";
    }
}
