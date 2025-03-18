import java.io.*;
import java.text.Normalizer;
import java.util.*;

public class B4_BayesianLM {

    public static Map<String, Integer> vocab = new HashMap<String, Integer>();
    public static Map<String, Integer> corpus = new HashMap<String, Integer>();
    public static Map<String, Integer> pairCorpus = new HashMap<String, Integer>();
    public static Double[] probs;
    public static Double[][] conditionalProbs;

    public static void readFile() {
        try {
            Vector<String> lines = new Vector<String>();
            File file = new File("UIT-ViOCD.txt");
            Scanner fileScanner = new Scanner(file, "UTF-8");

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                lines.addElement(line);
            }
            fileScanner.close();

            for (String line : lines) {
                line = Normalizer.normalize(line, Normalizer.Form.NFC);
                // remove line break \n, \r and tab \t
                line = line.replace("\n", "").replace("\r", "").replace("\t", "");
                // remove all leading spaces
                line = line.replaceAll("^\\s+", "");
                // remove all ending spaces
                line = line.replaceAll("\\s+$", "");
                // lowering
                line = line.toLowerCase();

                // collecting words
                int wordId = 0;
                String[] words = line.split("\\s+");
                for (String word : words) {
                    if (corpus.containsKey(word)) {
                        corpus.put(word, corpus.get(word) + 1);
                    } else {
                        vocab.put(word, wordId);
                        corpus.put(word, 1);
                        wordId += 1;
                    }
                }

                // collecting pairs of words
                for (int i = 0; i < words.length - 1; i++) {
                    String words_ij = words[i] + "_" + words[i + 1];
                    if (pairCorpus.containsKey(words_ij)) {
                        pairCorpus.put(words_ij, pairCorpus.get(words_ij) + 1);
                    } else {
                        pairCorpus.put(words_ij, 1);
                    }
                }
            }
            //check is "hàng" in a vocab
            // if (vocab.containsKey("hàng")) {
            //     System.out.println("hàng is in vocab");
            // } else {
            //     System.out.println("hàng is not in vocab");
            // }

        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("File not found!");
            fileNotFoundException.printStackTrace();
        }
    }

    public static void constructSingleProb() {
        // determine the total number of words in the dataset
        int totalWords = 0;
        for (Map.Entry<String, Integer> item : corpus.entrySet()) {
            totalWords += item.getValue();
        }

        // calculating the probability of each word
        probs = new Double[vocab.size()];
        for (Map.Entry<String, Integer> item : corpus.entrySet()) {
            String word = item.getKey();
            Integer wordCount = corpus.get(word);

            Integer wordId = vocab.get(word);

            // determining the P(w)
            probs[wordId] = (double) wordCount / totalWords;
        }
    }

    public static void constructConditionalProb() {
        int totalPairsOfWords = 0;
        for (Map.Entry<String, Integer> entry : pairCorpus.entrySet()) {
            totalPairsOfWords += entry.getValue();
        }
    
        Double[][] jointProbs = new Double[vocab.size()][vocab.size()];
    
        for (Map.Entry<String, Integer> entry_i : vocab.entrySet()) {
            String word_i = entry_i.getKey();
            int idx_i = entry_i.getValue();
    
            for (Map.Entry<String, Integer> entry_j : vocab.entrySet()) {
                String word_j = entry_j.getKey();
                int idx_j = entry_j.getValue();

                if (word_i == word_j) {
                    jointProbs[idx_i][idx_j] = 0.0;
                    continue;
                }
    
                String pairKey = word_i + "_" + word_j;
    
                if (pairCorpus.containsKey(pairKey)) {
                    int count = pairCorpus.get(pairKey);
                    jointProbs[idx_i][idx_j] = (double) count / totalPairsOfWords;
                } else {
                    jointProbs[idx_i][idx_j] = 1e-20;
                }
            }
        }
    
        conditionalProbs = new Double[vocab.size()][vocab.size()];
    
        for (Map.Entry<String, Integer> entry_i : vocab.entrySet()) {
            String word_i = entry_i.getKey();
            int idx_i = entry_i.getValue();
    
            for (Map.Entry<String, Integer> entry_j : vocab.entrySet()) {
                String word_j = entry_j.getKey();
                int idx_j = entry_j.getValue();

                String word_i_j = word_i + "_" + word_j;
                
                if (!pairCorpus.containsKey(word_i_j)) {
                    conditionalProbs[idx_i][idx_j] = 1e-10;
                    continue;
                }
    
                // determining the P(w_i | w_j)
                conditionalProbs[idx_i][idx_j] = jointProbs[idx_i][idx_j] / probs[idx_j];
    
                // handling edge cases
                if (Double.isNaN(conditionalProbs[idx_i][idx_j]) || Double.isInfinite(conditionalProbs[idx_i][idx_j])) {
                    conditionalProbs[idx_i][idx_j] = 1e-10;
                }
    
                conditionalProbs[idx_j][idx_i] = jointProbs[idx_j][idx_i] / probs[idx_i];
                if (Double.isNaN(conditionalProbs[idx_j][idx_i]) || Double.isInfinite(conditionalProbs[idx_j][idx_i])) {
                    conditionalProbs[idx_j][idx_i] = 1e-10;
                }
            }
        }
    }
    

    public static void training() {
        constructSingleProb();
        constructConditionalProb();
    }

    public static Vector<String> inferring(String w0) {
        Vector<String> res = new Vector<String>();
        res.add(w0);

        Integer prevIdx = vocab.get(w0);

        System.out.println(prevIdx);

        for (int t = 1; t < 5; t++) {
            String nextWord = "";
            double maxProb = -1.0;
            int nextIdx = -1;

            for (Map.Entry<String, Integer> entry : vocab.entrySet()) {
                int candidateIdx = entry.getValue();
                // double prob = conditionalProbs[candidateIdx][prevIdx];
                double prob = conditionalProbs[prevIdx][candidateIdx];

                if (prob > maxProb) {
                    maxProb = prob;
                    nextWord = entry.getKey();
                    nextIdx = candidateIdx;
                }
            }

            //print nextid
            System.out.println(nextIdx); 

            // if (nextWord.equals(""))
            //     break;
            res.add(nextWord);
            prevIdx = nextIdx;
        }

        return res;
    }

    public static void main(String[] args) throws Exception {
        readFile();
        training();
        Vector<String> predicted_words = inferring("quay");
        String sentence = String.join(" ", predicted_words);
        PrintStream out = new PrintStream(System.out, true, "UTF-8");
        out.println(sentence);

    }
}
