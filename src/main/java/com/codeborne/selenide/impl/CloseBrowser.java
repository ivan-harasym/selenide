package com.codeborne.selenide.impl;

import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.UnreachableBrowserException;

import java.util.logging.Logger;

import static com.codeborne.selenide.impl.Describe.describe;
import static java.util.logging.Level.FINE;

public class CloseBrowser implements Runnable {
  private static final Logger log = Logger.getLogger(CloseBrowser.class.getName());

  private final WebDriver webdriver;
  private final SelenideProxyServer proxy;

  public CloseBrowser(WebDriver webdriver, SelenideProxyServer proxy) {
    this.webdriver = webdriver;
    this.proxy = proxy;
  }

  @Override
  public void run() {
    try {
      log.info("Trying to close the browser " + describe(webdriver) + " ...");
      webdriver.quit();
    }
    catch (UnreachableBrowserException e) {
      // It happens for Firefox. It's ok: browser is already closed.
      log.log(FINE, "Browser is unreachable", e);
    }
    catch (WebDriverException cannotCloseBrowser) {
      log.severe("Cannot close browser normally: " + Cleanup.of.webdriverExceptionMessage(cannotCloseBrowser));
    }

    if (proxy != null) {
      log.info("Trying to shutdown " + proxy + " ...");
      proxy.shutdown();
    }
  }
}
