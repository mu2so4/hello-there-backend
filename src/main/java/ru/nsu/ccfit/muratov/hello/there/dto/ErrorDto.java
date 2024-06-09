package ru.nsu.ccfit.muratov.hello.there.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ErrorDto {
    private Date timestamp;
    private String message;
}
