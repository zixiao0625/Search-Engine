package search.analyzers;

import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.KVPair;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.ISet;
import search.models.Webpage;

import java.net.URI;

/**
 * This class is responsible for computing the 'page rank' of all available webpages.
 * If a webpage has many different links to it, it should have a higher page rank.
 * See the spec for more details.
 */
public class PageRankAnalyzer {
    private IDictionary<URI, Double> pageRanks;

    /**
     * Computes a graph representing the internet and computes the page rank of all
     * available webpages.
     *
     * @param webpages  A set of all webpages we have parsed.
     * @param decay     Represents the "decay" factor when computing page rank (see spec).
     * @param epsilon   When the difference in page ranks is less then or equal to this number,
     *                  stop iterating.
     * @param limit     The maximum number of iterations we spend computing page rank. This value
     *                  is meant as a safety valve to prevent us from infinite looping in case our
     *                  page rank never converges.
     */
    public PageRankAnalyzer(ISet<Webpage> webpages, double decay, double epsilon, int limit) {
        // Implementation note: We have commented these method calls out so your
        // search engine doesn't immediately crash when you try running it for the
        // first time.
        //
        // You should uncomment these lines when you're ready to begin working
        // on this class.

        // Step 1: Make a graph representing the 'internet'
        IDictionary<URI, ISet<URI>> graph = this.makeGraph(webpages);

        // Step 2: Use this graph to compute the page rank for each webpage
        this.pageRanks = this.makePageRanks(graph, decay, limit, epsilon);

        // Note: we don't store the graph as a field: once we've computed the
        // page ranks, we no longer need it!
    }

    /**
     * This method converts a set of webpages into an unweighted, directed graph,
     * in adjacency list form.
     *
     * You may assume that each webpage can be uniquely identified by its URI.
     *
     * Note that a webpage may contain links to other webpages that are *not*
     * included within set of webpages you were given. You should omit these
     * links from your graph: we want the final graph we build to be
     * entirely "self-contained".
     */
    private IDictionary<URI, ISet<URI>> makeGraph(ISet<Webpage> webpages) {
        IDictionary<URI, ISet<URI>> graph = new ChainedHashDictionary<URI, ISet<URI>>();
        ISet<URI> uri = new ChainedHashSet<URI>();
        for (Webpage web : webpages) {
            uri.add(web.getUri());
        }
        
        for (Webpage web : webpages) {
            ISet<URI> link = new ChainedHashSet<URI>();
            for (URI edge : web.getLinks()) {
               if (uri.contains(edge) && edge != web.getUri()) {
                   link.add(edge);
               }
            }
            graph.put(web.getUri(), link);
                    
        }
        return graph;
    }

    /**
     * Computes the page ranks for all webpages in the graph.
     *
     * Precondition: assumes 'this.graphs' has previously been initialized.
     *
     * @param decay     Represents the "decay" factor when computing page rank (see spec).
     * @param epsilon   When the difference in page ranks is less then or equal to this number,
     *                  stop iterating.
     * @param limit     The maximum number of iterations we spend computing page rank. This value
     *                  is meant as a safety valve to prevent us from infinite looping in case our
     *                  page rank never converges.
     */
    private IDictionary<URI, Double> makePageRanks(IDictionary<URI, ISet<URI>> graph,
                                                   double decay,
                                                   int limit,
                                                   double epsilon) {
        // Step 1: The initialize step should go here
        IDictionary<URI, Double> rank = new ChainedHashDictionary<URI, Double>();
        double initialWeight = 1.0/graph.size();
        for (KVPair<URI, ISet<URI>> web : graph) {
            rank.put(web.getKey(), initialWeight);
        }
        for (int i = 0; i < limit; i++) {
            // Step 2: The update step should go here
            IDictionary<URI, Double> newRank = new ChainedHashDictionary<URI, Double>();
            for (KVPair<URI, ISet<URI>> web : graph) {
                newRank.put(web.getKey(), 0.0);
            }
            for (KVPair<URI, ISet<URI>> web : graph) {
                double oldRank = rank.get(web.getKey());
                if (web.getValue().size() == 0) {
                    for (KVPair<URI, Double> page : rank) {
                        newRank.put(page.getKey(), newRank.get(page.getKey()) + decay * (oldRank / graph.size()));
                    }
                } else {
                    for (URI link : web.getValue()) {
                        newRank.put(link, newRank.get(link) + decay * oldRank / web.getValue().size());
                    }
                    
                }
                
                newRank.put(web.getKey(), newRank.get(web.getKey()) + (1-decay)/graph.size());
            }
            // Step 3: the convergence step should go here.
            // Return early if we've converged.
            boolean allConverge = true;
            for (KVPair<URI, Double> page : rank){
                double difference = Math.abs(newRank.get(page.getKey()) - rank.get(page.getKey()));
                if (difference > epsilon) {
                    allConverge = false;
                    break;
                }
            }
            if (allConverge) {
                return rank;
            } else {
                rank = newRank;
            }
        }
        return rank;
    }

    /** 
     * Returns the page rank of the given URI.
     *
     * Precondition: the given uri must have been one of the uris within the list of
     *               webpages given to the constructor.
     */
    public double computePageRank(URI pageUri) {
        // Implementation note: this method should be very simple: just one line!
        return pageRanks.get(pageUri);
    }
}
