//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.cifrazia.cats.api.buffers;

import com.cifrazia.cats.enumiration.CompressionType;
import com.google.common.primitives.UnsignedInteger;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class MessageByteBuf extends CatsByteBuf {
    private static final UnsignedInteger MAX_BYTE_BUF_SIZE = UnsignedInteger.valueOf(32000L);
    private File fileBuf = null;
    private ByteBuf byteBuf = null;
    private UnsignedInteger bufferSize;

    public MessageByteBuf(CompressionType compressionType) {
        super(compressionType);
    }

    public void initial(UnsignedInteger bufferSize) {
        try {
            if (bufferSize.longValue() > MAX_BYTE_BUF_SIZE.longValue()) {
                this.fileBuf = File.createTempFile("./temp/", ".cats");
                this.fileBuf.setWritable(true);
            } else {
                this.byteBuf = Unpooled.buffer(0);
            }

            super.emptyLength = bufferSize;
            this.bufferSize = bufferSize;
        } catch (Throwable var3) {
            try {
                throw var3;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public UnsignedInteger write(ByteBuf byteBuf) {
        try {
            if (super.write) {
                UnsignedInteger byteArraySize = null;
                ByteBuf tempByteBuf = null;
                if ((long)byteBuf.readableBytes() <= this.emptyLength.longValue()) {
                    tempByteBuf = byteBuf;
                    byteArraySize = UnsignedInteger.valueOf((long)byteBuf.readableBytes());
                } else {
                    tempByteBuf = byteBuf.readBytes(this.emptyLength.intValue());
                    byteArraySize = UnsignedInteger.valueOf(this.emptyLength.longValue());
                }

                byte[] result;
                if (this.fileBuf != null) {
                    result = new byte[tempByteBuf.readableBytes()];
                    tempByteBuf.readBytes(result);
                    FileUtils.writeByteArrayToFile(this.fileBuf, result, true);

                } else {
                    this.byteBuf.writeBytes(tempByteBuf);
                }

                tempByteBuf.clear();
                super.emptyLength = super.emptyLength.minus(byteArraySize);
                if (super.emptyLength.longValue() == 0L) {
                    super.write = false;

                    if(this.byteBuf == null) {
                        this.byteBuf = read();
                    }

                    switch (super.compressionType) {
                        case GZIP:
                            GzipCompressorInputStream in;
                            if (this.fileBuf != null) {
                                in = new GzipCompressorInputStream(Files.newInputStream(this.fileBuf.toPath()));
                                IOUtils.copy(in, new FileOutputStream(this.fileBuf, false));
                            } else {
                                in = new GzipCompressorInputStream(new ByteBufInputStream(this.byteBuf));
                                IOUtils.copy(in, new ByteBufOutputStream(this.byteBuf));
                            }
                            break;
                        case ZLIB:
                            result = new byte[this.byteBuf.readInt()];
                            byte[] bytes = new byte[this.byteBuf.readableBytes()];
                            this.byteBuf.readBytes(bytes);
                            Inflater decompresser = new Inflater();
                            decompresser.setInput(bytes, 0, bytes.length);
                            decompresser.inflate(result);
                            decompresser.end();
                            this.byteBuf = Unpooled.wrappedBuffer(result);
                    }
                }

                return byteArraySize;
            } else {
                return null;
            }
        } catch (Throwable var7) {
            try {
                throw var7;
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (DataFormatException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ByteBuf read() {
        try {
            return this.fileBuf == null ? this.byteBuf : this.byteBuf == null ? Unpooled.wrappedBuffer(FileUtils.readFileToByteArray(this.fileBuf) ) : this.byteBuf;
        } catch (Throwable var2) {
            try {
                throw var2;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
