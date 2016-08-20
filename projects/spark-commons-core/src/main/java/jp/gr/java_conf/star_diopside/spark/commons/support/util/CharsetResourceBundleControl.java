package jp.gr.java_conf.star_diopside.spark.commons.support.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * 指定した文字コードでプロパティファイルをロードするための {@link ResourceBundle.Control} サブクラス
 */
public class CharsetResourceBundleControl extends ResourceBundle.Control {

    private Charset charset;

    /**
     * コンストラクタ
     * 
     * @param charset プロパティファイルの文字コード
     */
    public CharsetResourceBundleControl(Charset charset) {
        this.charset = charset;
    }

    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
            throws IllegalAccessException, InstantiationException, IOException {

        if (format.equals("java.properties")) {
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName0(bundleName, "properties");
            if (resourceName == null) {
                return null;
            }
            try (InputStream stream = AccessController.doPrivileged((PrivilegedExceptionAction<InputStream>) () -> {
                if (reload) {
                    URL url = loader.getResource(resourceName);
                    if (url != null) {
                        URLConnection connection = url.openConnection();
                        if (connection != null) {
                            connection.setUseCaches(false);
                            return connection.getInputStream();
                        }
                    }
                } else {
                    return loader.getResourceAsStream(resourceName);
                }
                return null;
            })) {
                if (stream != null) {
                    try (InputStreamReader reader = new InputStreamReader(stream, charset)) {
                        return new PropertyResourceBundle(reader);
                    }
                }
            } catch (PrivilegedActionException e) {
                throw (IOException) e.getException();
            }
            return null;
        } else {
            return super.newBundle(baseName, locale, format, loader, reload);
        }
    }

    private String toResourceName0(String bundleName, String suffix) {
        if (bundleName.contains("://")) {
            return null;
        } else {
            return toResourceName(bundleName, suffix);
        }
    }
}
