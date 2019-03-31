package search.analyzers;

import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.KVPair;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;
import datastructures.interfaces.ISet;
import search.models.Webpage;

import java.net.URI;

/**
 * This class is responsible for computing how "relevant" any given document is
 * to a given search query.
 *
 * See the spec for more details.
 */
public class TfIdfAnalyzer {
    // This field must contain the IDF score for every single word in all
    // the documents.
    private IDictionary<String, Double> idfScores;

    // This field must contain the TF-IDF vector for each webpage you were given
    // in the constructor.
    //
    // We will use each webpage's page URI as a unique key.
    private IDictionary<URI, IDictionary<String, Double>> documentTfIdfVectors;
    private IDictionary<URI, Double> documentNorm;

    // Feel free to add extra fields and helper methods.

    public TfIdfAnalyzer(ISet<Webpage> webpages) {
        // Implementation note: We have commented these method calls out so your
        // search engine doesn't immediately crash when you try running it for the
        // first time.
        //
        // You should uncomment these lines when you're ready to begin working
        // on this class.

        this.idfScores = this.computeIdfScores(webpages);
        this.documentTfIdfVectors = this.computeAllDocumentTfIdfVectors(webpages);
        
    }

    // Note: this method, strictly speaking, doesn't need to exist. However,
    // we've included it so we can add some unit tests to help verify that your
    // constructor correctly initializes your fields.
    public IDictionary<URI, IDictionary<String, Double>> getDocumentTfIdfVectors() {
        return this.documentTfIdfVectors;
    }

    // Note: these private methods are suggestions or hints on how to structure your
    // code. However, since they're private, you're not obligated to implement exactly
    // these methods: feel free to change or modify these methods however you want. The
    // important thing is that your 'computeRelevance' method ultimately returns the
    // correct answer in an efficient manner.

    /**
     * Return a dictionary mapping every single unique word found
     * in every single document to their IDF score.
     */
    private IDictionary<String, Double> computeIdfScores(ISet<Webpage> pages) {
        IDictionary<String, Double> countFrequency = new ChainedHashDictionary<>();
        IDictionary<String, Double> idfscores = new ChainedHashDictionary<>();
        
        for (Webpage page : pages) {
            ISet<String> wordList = new ChainedHashSet<>();
            IList<String> words = page.getWords();
            for (String word : words) {
                if (!countFrequency.containsKey(word)) {
                    countFrequency.put(word, 1.0);
                    wordList.add(word);
                } else {
                    if (!wordList.contains(word)) {
                        wordList.add(word);
                        double value = countFrequency.get(word);
                        countFrequency.put(word, value + 1.0);
                    }    
                }
                    
            }
         }
        for (KVPair<String, Double> pair : countFrequency) {
            idfscores.put(pair.getKey(), Math.log(pages.size() / pair.getValue()));
        }
        return idfscores;  
            
    }

    /**
     * Returns a dictionary mapping every unique word found in the given list
     * to their term frequency (TF) score.
     *
     * The input list represents the words contained within a single document.
     */
    private IDictionary<String, Double> computeTfScores(IList<String> words) {
        IDictionary<String, Double> wordCount = new ChainedHashDictionary<>();
        IDictionary<String, Double> tfScores = new ChainedHashDictionary<>();
        for (String word : words) {
            if (wordCount.containsKey(word))
            {
                double value = wordCount.get(word);
                wordCount.put(word, value + 1.0);
            } else {
                wordCount.put(word, 1.0); 
            }       
        }
        for (KVPair<String, Double> pair : wordCount) {
            tfScores.put(pair.getKey(), pair.getValue() / words.size());
        }
        return tfScores;    
    }

    /**
     * See spec for more details on what this method should do.
     */
    private IDictionary<URI, IDictionary<String, Double>> computeAllDocumentTfIdfVectors(ISet<Webpage> pages) {
        IDictionary<URI, IDictionary<String, Double>> allVectors = new ChainedHashDictionary<>();
        IDictionary<URI, Double> pageNorm = new ChainedHashDictionary<>();
        for (Webpage page : pages) {
            double norm = 0;
            URI url = page.getUri();
            IDictionary<String, Double> vectors = new ChainedHashDictionary<>();
            IList<String> words = page.getWords();
            for (KVPair<String, Double> pair : this.computeTfScores(words)) {
                String word = pair.getKey();
                vectors.put(word, pair.getValue() * this.idfScores.get(word));
                norm += vectors.get(word) * vectors.get(word);
            }
            pageNorm.put(url, Math.sqrt(norm));
            allVectors.put(url, vectors);       
        }
        this.documentNorm = pageNorm;
        return allVectors;
    }

    /**
     * Returns the cosine similarity between the TF-IDF vector for the given query and the
     * URI's document.
     *
     * Precondition: the given uri must have been one of the uris within the list of
     *               webpages given to the constructor.
     */
    public Double computeRelevance(IList<String> query, URI pageUri) {
        // Note: The pseudocode we gave you is not very efficient. When implementing,
        // this method, you should:
        //
        // 1. Figure out what information can be precomputed in your constructor.
        //    Add a third field containing that information.
        //
        // 2. See if you can combine or merge one or more loops.
        IDictionary<String, Double> documentVector = this.documentTfIdfVectors.get(pageUri);
        double dNorm = this.documentNorm.get(pageUri);
        

        IDictionary<String, Double> queryVector = new ChainedHashDictionary<>();
        IDictionary<String, Double> queryTFScores = this.computeTfScores(query);
        double queryNorm = 0.0;
        for (KVPair<String, Double> pair : queryTFScores) {
            String word = pair.getKey();
            if (this.idfScores.containsKey(word)) {
                queryVector.put(word, pair.getValue() * this.idfScores.get(word));
            } else {
                queryVector.put(word, 0.0);
            }
            queryNorm += queryVector.get(word) * queryVector.get(word);
        }
        
        double numerator = 0.0;
        for (String word : query) {
            double docWordScore = 0.0; 
            double queryWordScore = queryVector.get(word);
            if (documentVector.containsKey(word)) {
                docWordScore = documentVector.get(word);
            } 
            numerator += docWordScore * queryWordScore;
        }
       double denominator = dNorm * Math.sqrt(queryNorm);
       if (denominator != 0) {
           return numerator / denominator;
       } else {
           return 0.0;
       }
    }
}
