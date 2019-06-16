package com.streamer.worker.log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.streamer.service.core.StreamerConstant;

public class LogViewer {

	public static String readLog(String name, long n) {
		String job = StreamerConstant.FORMAT_JOB_NAME(name);
		if (name.equals("_")) {
			job = null;
		}
		File file = new File(StreamerConstant.LOG_NAME);
		return readLastNLine(job, file, n);

	}

	public static String readLastNLine(String seek, File file, long n) {
		List<String> result = new ArrayList<String>();
		long count = 0;
		if (!file.exists() || file.isDirectory() || !file.canRead()) {
			return "";
		}
		RandomAccessFile fileRead = null;
		try {
			// 使用读模式
			fileRead = new RandomAccessFile(file, "r");
			// 读取文件长度
			long length = fileRead.length();
			if (length == 0L) {
				return "";
			} else {
				long pos = length - 1;
				while (pos > 0) {
					pos--;
					fileRead.seek(pos);
					if (fileRead.readByte() == '\n') {

						String line = new String(fileRead.readLine().getBytes(Charset.forName("UTF-8")));

						if (StringUtils.isEmpty(seek)) {
							result.add(line);
							count++;
						} else {
							if (line.contains(seek)) {
								result.add(line);
								count++;
							} else {
								continue;
							}
						}
						if (count >= StreamerConstant.MAX_LINE || count == n) {
							break;
						}
					}
				}
				if (pos == 0) {
					fileRead.seek(0);
					String line = fileRead.readLine();
					if (StringUtils.isEmpty(seek)) {
						result.add(line);
					} else {
						if (line.contains(seek)) {
							result.add(line);
						}
					}
				}
			}
		} catch (IOException e) {
		} finally {
			if (fileRead != null) {
				try {
					fileRead.close();
				} catch (Exception e) {
				}
			}
		}
		Collections.reverse(result);
		return StringUtils.join(result, "\n");
	}
}
