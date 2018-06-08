package com.assessment.main;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.assessment.access.TaskAccessBean;
import com.assessment.monitor.FileMonitor;

public class Testings {

	public static void main(String[] args) throws IOException, InterruptedException {

		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext("com.assessment.access");
		TaskAccessBean bean = ctx.getBean(TaskAccessBean.class);
		//System.out.println(bean.findAll());
		FileMonitor fm = new FileMonitor();
		fm.monitorFolder();

	}

	private static void monitorFiles() throws IOException, InterruptedException{
		Path faxFolder = Paths.get("./assessment/csv_files/");
		WatchService watchService = FileSystems.getDefault().newWatchService();
		faxFolder.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

		boolean valid = true;
		do {
			WatchKey watchKey = watchService.take();

			for (WatchEvent event : watchKey.pollEvents()) {
				WatchEvent.Kind kind = event.kind();
				if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) {
					String fileName = event.context().toString();
					System.out.println("File Created:" + fileName);
				}
			}
			valid = watchKey.reset();

		} while (valid);
	}
}
