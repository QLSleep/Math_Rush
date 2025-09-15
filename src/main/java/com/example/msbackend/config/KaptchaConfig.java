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
  private int imageWidth;
  private int imageHeight;
  private String charString;
  private int charLength;
  private String fontNames;
  private int fontSize;
  private String fontColor;
  private String backgroundColorFrom;
  private String backgroundColorTo;
  private String noiseColor;
  private String noiseImpl;
  private String border;
  private String borderColor;
  private int borderThickness;


  @Bean
  public DefaultKaptcha kaptchaProducer() {
    Properties properties = new Properties();
    properties.setProperty("kaptcha.image.width", String.valueOf(imageWidth));
    properties.setProperty("kaptcha.image.height", String.valueOf(imageHeight));
    properties.setProperty("kaptcha.textproducer.char.string", charString);
    properties.setProperty("kaptcha.textproducer.char.length", String.valueOf(charLength));
    properties.setProperty("kaptcha.textproducer.font.names", fontNames);
    properties.setProperty("kaptcha.textproducer.font.size", String.valueOf(fontSize));
    properties.setProperty("kaptcha.textproducer.font.color", fontColor);
    properties.setProperty("kaptcha.background.color.from", backgroundColorFrom);
    properties.setProperty("kaptcha.background.color.to", backgroundColorTo);
    properties.setProperty("kaptcha.noise.color", noiseColor);
    properties.setProperty("kaptcha.noise.impl", noiseImpl);
    properties.setProperty("kaptcha.border", border);
    properties.setProperty("kaptcha.border.color", borderColor);
    properties.setProperty("kaptcha.border.thickness", String.valueOf(borderThickness));

    DefaultKaptcha kaptcha = new DefaultKaptcha();
    kaptcha.setConfig(new Config(properties));
    return kaptcha;
  }
}