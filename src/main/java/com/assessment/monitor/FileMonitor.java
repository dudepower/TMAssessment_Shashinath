package com.assessment.monitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.StringUtils;

import com.assessment.access.BaseDataAccessBean;
import com.assessment.access.Task;
import com.assessment.access.TaskAccessBean;
import com.assessment.access.Team;
import com.assessment.access.TeamAccessBean;
import com.assessment.access.TeamSkill;
import com.assessment.access.TeamSkillAccessBean;
import com.mysql.fabric.xmlrpc.base.Array;

public class FileMonitor {

	private TaskAccessBean taskAccessBean;

	private TeamAccessBean teamAccessBean;

	private TeamSkillAccessBean teamSkillAccessBean;

	public void monitorFolder() throws IOException, InterruptedException {
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
					if(fileName.equals("task.csv") || fileName.equals("team_skill.csv") || fileName.equals("team.csv")){
						onNewFile(new File("./assessment/csv_files/"+fileName));
					}
				}
			}
			valid = watchKey.reset();

		} while (valid);
	}

	private static final int NUM_THREADS = 4;
	private static final int QUEUE_SIZE = 1000;

	private final BlockingQueue<File> fileQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);

	public FileMonitor() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext("com.assessment.access");
		taskAccessBean = ctx.getBean(TaskAccessBean.class);
		teamAccessBean = ctx.getBean(TeamAccessBean.class);
		teamSkillAccessBean = ctx.getBean(TeamSkillAccessBean.class);
		
		// Create our 4 processors
		ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);

		for (int i = 0; i < NUM_THREADS; ++i) {
			executorService.execute(new FileProcessor());
		}
	}

	// When a file comes in to your WatchService
	private void onNewFile(File file) {
		fileQueue.add(file);
	}

	private class FileProcessor implements Runnable {
		@Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					File file = fileQueue.take(); // blocks
					process(file);
					Thread.sleep(500);
				} catch (InterruptedException ex) {
					System.out.println(Thread.currentThread().getName()+":\tInterupted process");
					ex.printStackTrace();
				}
				catch (IOException e) {
					System.out.println(Thread.currentThread().getName()+":\tInterupted process");
				
					e.printStackTrace();
				}
			}
		}

		private void process(File file) throws IOException {
			System.out.println(Thread.currentThread().getName()+":\tBegin process");
			//FileReader fr = new FileReader(file);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			int i = 0;
			while ((line = reader.readLine()) != null) {
				if (i == 0) {
					System.out.println(Thread.currentThread().getName()+":\tBegin process"+i);
					i++;
					continue;
				}
				final String[] sTabs = StringUtils.split(StringUtils.replace(line, "\"", ""), ",");
				switch (file.getName()) {
				case "team.csv":
					Team team = new Team();
					team.setTeamId(sTabs[0]);
					taskAccessBean.save(team);
					break;
				case "task.csv":
					Task task = new Task();
					task.setTaskId(sTabs[0]);
					task.setSkill(sTabs[1]);
					teamAccessBean.save(task);
					break;
				case "team_skill.csv":
					TeamSkill teamSkill = new TeamSkill();
					System.out.println(sTabs[0]);
					teamSkill.setTeamId(sTabs[0]);
					teamSkill.setSkill(sTabs[1]);
					teamSkillAccessBean.save(teamSkill);
					break;
				}
			}
			//fr.close();
			reader.close();
			file.delete();
			System.out.println(Thread.currentThread().getName()+":\tEnd process");
		}
	}

}
