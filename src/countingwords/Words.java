package countingwords;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

public class Words {
	
	private static final String WORD_SEPARATOR = " "; 

	public SortedMap<Long, String> getMostFrequentWords(String fileOrFolder,
			int wordsCount) throws Exception {
		Path path = Paths.get(fileOrFolder);
		if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS))
			throw new Exception("file is symlink or does not exist");
		if (!Files.isReadable(path))
			throw new Exception("no access to " + path);
		if (Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS))
			return filterFrequentWords(
					wordsInFileMap(readTextFile(fileOrFolder)), wordsCount);
		if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
			List<Map<String, Long>> resList = new LinkedList<Map<String, Long>>();
			traverseFolderRecursivly(fileOrFolder, resList);
			return filterFrequentWords(reduce(resList), wordsCount);
		}

		return null;
	}

	private SortedMap<Long, String> filterFrequentWords(
			Map<String, Long> wordsMap, int wordsCount) {
		SortedMap<Long, String> tmp = new TreeMap<Long, String>();
		for (Entry<String, Long> e : wordsMap.entrySet()) {
			tmp.put(e.getValue(), e.getKey());
		}
		SortedMap<Long, String> result = new TreeMap<Long, String>();
		int count = 0;
		for (Entry<Long, String> e : tmp.entrySet()) {
			result.put(e.getKey(), e.getValue());
			if (++count == wordsCount)
				break;
		}

		return result;
	}

	private Map<String, Long> reduce(List<Map<String, Long>> wordsMapsList) {
		Map<String, Long> map = new HashMap<String, Long>();
		for (Map<String, Long> m : wordsMapsList)
			for (Entry<String, Long> e : m.entrySet()) {
				String word = e.getKey();
				if (!map.keySet().contains(word))
					map.put(word, e.getValue());
				else
					map.put(word, map.get(word) + e.getValue());
			}
		return map;
	}

	private void traverseFolderRecursivly(String folderToScan,
			List<Map<String, Long>> listToFill) {
		File folder = new File(folderToScan);
		for (File f : folder.listFiles()) {
			Path path = Paths.get(f.getPath());
			if (Files.isReadable(path)
					&& Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) {
				listToFill.add(wordsInFileMap(readTextFile(f.getPath())));
			}
			if (Files.isReadable(path)
					&& Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
				traverseFolderRecursivly(f.getPath(),listToFill);
			}
		}
	}

	private List<String> readTextFile(String pathToFile) {
		List<String> text = new LinkedList<String>();
		try (Scanner sc = new Scanner(new File(pathToFile))) {
			while (sc.hasNextLine()) {
				text.add(sc.nextLine());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return text;
	}

	private Map<String, Long> wordsInFileMap(List<String> text) {
		Map<String, Long> map = new HashMap<String, Long>();
		for (String line : text)
			for (String word : line.trim().split(WORD_SEPARATOR)) {
				if (!map.keySet().contains(word))
					map.put(word, 1l);
				else
					map.put(word, map.get(word) + 1);
			}
		return map;
	}

	
	
	
}
