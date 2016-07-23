package net.tenorite.util;

import org.immutables.value.Value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Johan Siebens
 */
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
@Value.Style(
    get = {"is*", "get*"},
    visibility = Value.Style.ImplementationVisibility.PACKAGE,
    implementationNestedInBuilder = true
)
public @interface ImmutableStyle {

}