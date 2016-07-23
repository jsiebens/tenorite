package net.tenorite.core;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Johan Siebens
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class NotAvailableException extends RuntimeException {

}
