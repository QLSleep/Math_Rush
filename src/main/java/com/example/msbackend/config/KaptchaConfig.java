package com.example.msbackend.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Data
@Configuration
@ConfigurationProperties(prefix = "kaptcha")
public class KaptchaConfig {
  private ImageProperties image;
  private TextProducerProperties textproducer;
  private BackgroundProperties background;
  private NoiseProperties noise;
  private BorderProperties border;

  @Data
  public static class ImageProperties {
    private int width;
    private int height;
  }

  @Data
  public static class TextProducerProperties {
    private CharProperties charProps;
    private FontProperties font;
  }

  @Data
  public static class CharProperties {
    private String string;
    private int length;
  }

  @Data
  public static class FontProperties {
    private String names;
    private int size;
    private String color;
  }

  @Data
  public static class BackgroundProperties {
    private ColorProperties color;
  }

  @Data
  public static class ColorProperties {
    private String from;
    private String to;
  }

  @Data
  public static class NoiseProperties {
    private String color;
    private String impl;
  }

  @Data
  public static class BorderProperties {
    private boolean enabled;
    private String color;
    private int thickness;
  }

  @Bean
  public DefaultKaptcha kaptchaProducer() {
    Properties properties = new Properties();
    if (image != null) {
      properties.setProperty("kaptcha.image.width", String.valueOf(image.getWidth()));
      properties.setProperty("kaptcha.image.height", String.valueOf(image.getHeight()));
    }
    if (textproducer != null && textproducer.getCharProps() != null) {
      properties.setProperty("kaptcha.textproducer.char.string", textproducer.getCharProps().getString());
      properties.setProperty("kaptcha.textproducer.char.length", String.valueOf(textproducer.getCharProps().getLength()));
    }
    if (textproducer != null && textproducer.getFont() != null) {
      properties.setProperty("kaptcha.textproducer.font.names", textproducer.getFont().getNames());
      properties.setProperty("kaptcha.textproducer.font.size", String.valueOf(textproducer.getFont().getSize()));
      properties.setProperty("kaptcha.textproducer.font.color", textproducer.getFont().getColor());
    }
    if (background != null && background.getColor() != null) {
      properties.setProperty("kaptcha.background.color.from", background.getColor().getFrom());
      properties.setProperty("kaptcha.background.color.to", background.getColor().getTo());
    }
    if (noise != null) {
      properties.setProperty("kaptcha.noise.color", noise.getColor());
      properties.setProperty("kaptcha.noise.impl", noise.getImpl());
    }
    if (border != null) {
      properties.setProperty("kaptcha.border", border.isEnabled() ? "yes" : "no");
      properties.setProperty("kaptcha.border.color", border.getColor());
      properties.setProperty("kaptcha.border.thickness", String.valueOf(border.getThickness()));
    }

    DefaultKaptcha kaptcha = new DefaultKaptcha();
    kaptcha.setConfig(new Config(properties));
    return kaptcha;
  }
}