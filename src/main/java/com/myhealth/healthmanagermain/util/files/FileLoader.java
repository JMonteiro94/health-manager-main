package com.myhealth.healthmanagermain.util.files;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

@Slf4j
@UtilityClass
public class FileLoader {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  static {
    objectMapper.registerModule(new JavaTimeModule());
  }

  public static <T> T loadJsonResource(final String resourcePath,
      final TypeReference<T> typeReference) {
    final Optional<InputStream> source = getStream(resourcePath);
    if (source.isPresent()) {
      try {
        return objectMapper.readValue(source.get(), typeReference);
      } catch (final IOException e) {
        log.info("Error while getting response for mock {}", resourcePath);
        throw new IllegalStateException("Error while getting response from mock", e);
      }
    }
    return null;
  }

  @SneakyThrows
  public static <T> T loadXmlResource(final String resourcePath, final Class<T> resultClass) {
    final Optional<InputStream> source = getStream(resourcePath);
    if (source.isPresent()) {
      try {
        final JAXBContext context = JAXBContext.newInstance(resultClass);
        final SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        spf.setNamespaceAware(true);
        final XMLReader xmlReader = spf.newSAXParser().getXMLReader();
        final InputSource inputSource = new InputSource(source.get());
        final SAXSource saxSource = new SAXSource(xmlReader, inputSource);
        final Unmarshaller unmarshaller = context.createUnmarshaller();
        JAXBElement<T> element = unmarshaller.unmarshal(saxSource, resultClass);
        return element.getValue();
      } catch (final JAXBException e) {
        log.info("Error while getting response for mock {} class {}", resourcePath,
            resultClass.getName());
        throw new IllegalStateException("Error while getting response from mock", e);
      }
    }
    return null;
  }

  public static boolean resourceExists(final String resourcePath) {
    return getStream(resourcePath).isPresent();
  }

  private static Optional<InputStream> getStream(final String resourceName) {
    final InputStream resource = FileLoader.class.getResourceAsStream(resourceName);
    if (resource == null) {
      return Optional.empty();
    }
    return Optional.of(resource);
  }
}
