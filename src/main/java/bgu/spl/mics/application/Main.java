package bgu.spl.mics.application;
import bgu.spl.mics.*;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.*;


/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */

public class Main {

	public static void main(String[] args) throws FileNotFoundException, InterruptedException {
		Diary d = Diary.getInstance();
		jsonObject jsonObject = new jsonObject();
		String path = args[0];
		Gson gson = new Gson();
		try {
			Reader reader = Files.newBufferedReader(Paths.get(String.valueOf(path)));
			jsonObject = gson.fromJson(reader, jsonObject.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		runAllThreads(jsonObject.getAttack(), jsonObject.getEwoks(), jsonObject.getLando(), jsonObject.getR2D2());

		try {
			FileWriter write = new FileWriter(args[1]);
			gson.toJson(d, write);
			write.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void runAllThreads(Attack[] attacks, int numOfEwoks, long landoTime, long r2d2Time) {
		MicroService leia = new LeiaMicroservice(attacks);
		MicroService HanSolo = new HanSoloMicroservice();
		MicroService C3P0 = new C3POMicroservice();
		MicroService R2D2 = new R2D2Microservice(r2d2Time);
		MicroService Lando = new LandoMicroservice(landoTime);

		Ewoks.getInstance().setEwoksList(numOfEwoks);

		Thread leiaT = new Thread(leia);
		Thread HanT = new Thread(HanSolo);
		Thread C3POT = new Thread(C3P0);
		Thread R2D2T = new Thread(R2D2);
		Thread landoT = new Thread(Lando);

		leiaT.start();
		HanT.start();
		C3POT.start();
		R2D2T.start();
		landoT.start();

		try {
			leiaT.join();
			HanT.join();
			C3POT.join();
			R2D2T.join();
			landoT.join();
		} catch (InterruptedException interruptedException) {
			interruptedException.printStackTrace();
		}
	}
}