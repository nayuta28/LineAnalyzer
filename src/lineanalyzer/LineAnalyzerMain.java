package lineanalyzer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.stream.Collectors;

public class LineAnalyzerMain {

	private static Map<String, Long> memberCountMap = new LinkedHashMap<>();
	private static Long totalTalkCount;

	public static void main(String[] args) {

		try (Scanner scan = new Scanner(System.in)) {

			System.out.println("ファイルパスを入力してください");
			String filePath = scan.nextLine();
			List<String> allLinesList = readFile(filePath);

			countMemberTalk(allLinesList);
			showMemberTalkCount();

			System.out.println("検索したい文字列を入力してください");
			String message = scan.nextLine();
			countMessage(message, allLinesList);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static List<String> readFile(String filePath) throws IOException {
		Path path = Paths.get(filePath);
		return Files.readAllLines(path, StandardCharsets.UTF_8);
	}

	private static void countMemberTalk(List<String> allLines) {
		memberCountMap = allLines.stream().map(s -> s.split("\t")).filter(s -> s.length >= 3)
				.collect(Collectors.groupingBy(s -> s[1], Collectors.counting()));
		memberCountMap = sortMemberMap();
		totalTalkCount = memberCountMap.values().stream().mapToLong(l -> l).sum();
	}

	private static void showMemberTalkCount() {
		System.out.println("トーク履歴内のアカウントごとの発言数");
		memberCountMap.forEach((key, value) -> System.out.println(key + " : " + value + " : " + formatRate(value)));
		System.out.println("合計発言数 ： " + totalTalkCount);
	}

	private static void countMessage(String word, List<String> allList) {
		long wordCount = allList.stream().filter(s -> s.indexOf(word) > 0).count();
		System.out.println("「" + word + "」は、" + wordCount + "回使われています");
	}

	private static String formatRate(long value) {
		DecimalFormat dFormat = new DecimalFormat("###.##%");
		return dFormat.format(value / Double.parseDouble(String.valueOf(totalTalkCount)));
	}

	private static LinkedHashMap<String, Long> sortMemberMap() {
		LinkedHashMap<String, Long> sortedMap = new LinkedHashMap<String, Long>();
		ArrayList<Entry<String, Long>> entries = new ArrayList<>(memberCountMap.entrySet());
		Collections.sort(entries, new Comparator<Entry<String, Long>>() {
			@Override
			public int compare(Entry<String, Long> e1, Entry<String, Long> e2) {
				return e2.getValue().compareTo(e1.getValue());
			}
		});
		entries.stream().forEach(e -> sortedMap.put(e.getKey(), e.getValue()));
		return sortedMap;
	}
}
