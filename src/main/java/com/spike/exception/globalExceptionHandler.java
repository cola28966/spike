package com.spike.exception;

import com.spike.result.CodeMsg;
import com.spike.result.Result;
import org.apache.ibatis.annotations.Param;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@ControllerAdvice
@ResponseBody
public class globalExceptionHandler {

   /* @ExceptionHandler(value = Exception.class)
    public Result<String> exceptionHandler(HttpServletRequest request,Exception e ){
        if(e instanceof BindException){
            BindException ex = (BindException)e;
            List<ObjectError> errors = ex.getAllErrors();
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
        }
        else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }*/
      @ExceptionHandler(value = Exception.class)
    public Result<String> exceptionHandler(Exception e ){
          if(e instanceof GlobalException) {
              e.printStackTrace();
              GlobalException ex = (GlobalException)e;
              return Result.error(ex.getCm());
          }
        else if(e instanceof BindException){
            BindException ex = (BindException)e;
            List<ObjectError> errors = ex.getAllErrors();
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
        }
        else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }
}
