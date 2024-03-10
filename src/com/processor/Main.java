package com.processor;

import java.io.*;
import java.util.*;

public class Main {

    static Scanner inputTerminal = new Scanner(System.in);
    public static final String FILE_DIRECTORY = "C:\\temp\\direktori\\";

    public static void main(String[] args) throws IOException {

        mainMenu();

    }

    private static void dashLine() {
        System.out.println("------------------------------------------------------------------");
    }

    private static void header() {
        dashLine();
        System.out.println("Aplikasi Pengolah Nilai Siswa");
        dashLine();
    }

    private static void mainMenu() throws IOException {

        header();
        System.out.println("Letakan file csv dengan nama file data_sekolah ");
        System.out.println("di direktori berikut: c:/temp/direktori");
        System.out.println("\npilih menu:");
        System.out.println("1.\tGenerate txt untuk menampilkan data modus");
        System.out.println("2.\tGenerate txt untuk menampilkan nilai rata-rata, median, modus");
        System.out.println("3.\tGenerate kedua file");
        System.out.println("0.\tExit");

        String userChoice = inputUser();
        HashMap<String, List<Integer>> data;

        switch (userChoice) {
            case "1":
                data = getSchoolData();
                generateModus(data, true);
                jobDone();
                break;
            case "2":
                data = getSchoolData();
                generateModusMean(data, false);
                jobDone();
                break;
            case "3":
                data = getSchoolData();
                generateModusMean(data, true);
                jobDone();
                break;
            case "0":
                applicationClosed();
                break;
            default:
                wrongInput();
        }

    }

    private static void generateModusMean(HashMap<String, List<Integer>> data, boolean generateFileModus) throws IOException {
        List<Integer> allClassData = data.get("SemuaKelas");

//        Sort number
        Collections.sort(allClassData);
        double median;
        int lenData = allClassData.toArray().length;

//        Find median
        if (lenData % 2 == 0) {
//            if even then avg 2 median grades
            median = ((double) allClassData.get(lenData / 2) + (double) allClassData.get(lenData / 2 - 1)) / 2;
        } else {
//            if odd get median grade
            median = (double) allClassData.get(lenData / 2);
        }

//        find modus

        int modus = generateModus(data, generateFileModus);

//        Find Mean
        double sumAllData = sumAllDataFromList(allClassData);
        double mean = sumAllData / lenData;

        generateMeanModusFile(mean, median, modus);


    }

    private static void generateMeanModusFile(double mean, double median, int modus) throws IOException {
        try (FileWriter fileOutput = new FileWriter(FILE_DIRECTORY + "data_sekolah_modus_mean.txt");
             BufferedWriter bufferedWriter = new BufferedWriter(fileOutput)) {

            String line ="Berikut Hasil Pengolahan Nilai :" +
                    "\n\n" +
                    "Berikut hasil sebaran data nilai\n" +
                    String.format("Mean\t\t=\t%.2f\n",mean) +
                    String.format("Median\t\t=\t%.2f\n",median) +
                    String.format("Modus\t\t=\t%d\n",modus);

            bufferedWriter.write(line, 0, line.length());
            bufferedWriter.flush();

        }
    }

    private static int sumAllDataFromList(List<Integer> allClassData) {
        int sum = 0;
        for (int data : allClassData) {
            sum += data;
        }
        return sum;
    }

    private static int generateModus(HashMap<String, List<Integer>> data, boolean generateFiles) throws IOException {

        List<Integer> allClassData = data.get("SemuaKelas");

        Map<Integer, Integer> frequencyData = constructFrequencyData(allClassData);


        int lessThanSixFreq = frequencyData.get(0) + frequencyData.get(1) + frequencyData.get(2) + frequencyData.get(3) + frequencyData.get(4) + frequencyData.get(5);

        int modus = 0;
        int freq = 0;
        for (Map.Entry<Integer, Integer> set : frequencyData.entrySet()) {
            if (set.getValue() >= freq) {
                modus = set.getKey();
                freq = set.getValue();
            }
        }

        if (generateFiles) generateModusFile(lessThanSixFreq, frequencyData);


        return modus;

    }

    private static Map<Integer, Integer> constructFrequencyData(List<Integer> allClassData) {
        Map<Integer, Integer> frequencyData = new HashMap<>();
        frequencyData.put(0, Collections.frequency(allClassData, 0));
        frequencyData.put(1, Collections.frequency(allClassData, 1));
        frequencyData.put(2, Collections.frequency(allClassData, 2));
        frequencyData.put(3, Collections.frequency(allClassData, 3));
        frequencyData.put(4, Collections.frequency(allClassData, 4));
        frequencyData.put(5, Collections.frequency(allClassData, 5));
        frequencyData.put(6, Collections.frequency(allClassData, 6));
        frequencyData.put(7, Collections.frequency(allClassData, 7));
        frequencyData.put(8, Collections.frequency(allClassData, 8));
        frequencyData.put(9, Collections.frequency(allClassData, 9));
        frequencyData.put(10, Collections.frequency(allClassData, 10));
        return frequencyData;
    }

    private static void generateModusFile(int lessThanSixCounter, Map<Integer, Integer> frequencyData) throws IOException {

        try (FileWriter fileOutput = new FileWriter(FILE_DIRECTORY +"data_sekolah_modus.txt");
             BufferedWriter bufferedWriter = new BufferedWriter(fileOutput)) {
            String line = "Berikut Hasil Pengolahan Nilai :" +
                    "\n\n" +
                    "Nilai\t\t\t|\tFrekuensi\n" +
                    String.format("Kurang dari 6\t\t|\t%d\n", lessThanSixCounter) +
                    String.format("6\t\t\t|\t%d\n", frequencyData.get(6)) +
                    String.format("7\t\t\t|\t%d\n", frequencyData.get(7)) +
                    String.format("8\t\t\t|\t%d\n", frequencyData.get(8)) +
                    String.format("9\t\t\t|\t%d\n", frequencyData.get(9)) +
                    String.format("10\t\t\t|\t%d\n", frequencyData.get(10));

            bufferedWriter.write(line, 0, line.length());
            bufferedWriter.flush();


        }
    }

    private static HashMap<String, List<Integer>> getSchoolData() throws IOException {
        HashMap<String, List<Integer>> data = new HashMap<>();
        List<Integer> allClassGrades = new ArrayList<>();

        try (FileReader fileInput = new FileReader(FILE_DIRECTORY +"data_sekolah.csv");
             BufferedReader bufferedReader = new BufferedReader(fileInput)) {

            //        Read file
            String line = bufferedReader.readLine();

            while (line != null) {
                StringTokenizer stringTokenizer = new StringTokenizer(line, ";");
                String className = stringTokenizer.nextToken();
                List<Integer> classGrades = new ArrayList<>();
                while (stringTokenizer.hasMoreTokens()) {
                    int grade = Integer.parseInt(stringTokenizer.nextToken());
                    classGrades.add(grade);
                }

                data.put(className, classGrades);
                allClassGrades.addAll(classGrades);

                line = bufferedReader.readLine();
            }
        } catch (FileNotFoundException exception) {
            fileNotFound();
        }
        data.put("SemuaKelas", allClassGrades);

        return data;
    }

    private static String inputUser() {

        System.out.println("\nPilihan menu : ");

        return inputTerminal.next();
    }

    private static void wrongInput() throws IOException {
        header();
        System.out.println("\nMenu tidak ditemukan");
        System.out.println("Pilih angka menu yang tertera pada daftar menu\n");
        menusOnRun();
        String userChoice = inputUser();
        doChoiceMenusOnRun(userChoice);

    }

    private static void jobDone() throws IOException {
        header();
        System.out.println("File telah di generate di c:/temp/direktori");
        System.out.println("silahkan cek\n");

        menusOnRun();
    }

    private static void menusOnRun() throws IOException {
        System.out.println("0.\tExit");
        System.out.println("1.\tKembali ke menu utama");

        String userChoice = inputUser();

        doChoiceMenusOnRun(userChoice);
    }

    private static void doChoiceMenusOnRun(String userChoice) throws IOException {
        switch (userChoice) {
            case "1":
                mainMenu();
                break;
            case "0":
                applicationClosed();
                break;
            default:
                wrongInput();
        }
    }

    private static void fileNotFound() throws IOException {
        header();
        System.out.println("File tidak ditemukan\n");

        menusOnRun();
    }

    private static void applicationClosed() {

        header();
        System.out.println("\n\n\nAplikasi ditutup\n");
        dashLine();
        inputTerminal.close();
        System.exit(200);
    }


}