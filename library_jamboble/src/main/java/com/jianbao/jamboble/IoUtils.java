package com.jianbao.jamboble;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public final class IoUtils {
    public static final int DEFAULT_BUFFER_SIZE = 32768;
    public static final int DEFAULT_IMAGE_TOTAL_SIZE = 512000;
    public static final int CONTINUE_LOADING_PERCENTAGE = 75;

    private IoUtils() {
    }

    public static void readAndCloseStream(InputStream is) {
        byte[] bytes = new byte['耀'];

        try {
            while(true) {
                if (is.read(bytes, 0, 32768) != -1) {
                    continue;
                }
            }
        } catch (IOException var6) {
        } finally {
            closeSilently(is);
        }

    }

    public static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception var2) {
            }
        }

    }

    public interface CopyListener {
        boolean onBytesCopied(int var1, int var2);
    }
}
