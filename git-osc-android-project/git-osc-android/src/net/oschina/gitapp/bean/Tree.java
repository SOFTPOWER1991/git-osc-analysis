package net.oschina.gitapp.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 代码树
 * @created 2014-06-04 
 * @author 火蚁
 *
 */
@SuppressWarnings("serial")
public class Tree implements Serializable {
	
	private List<CodeTree> tree;
	private String sha;
	
	/**
	 * @return tree
	 */
	public List<CodeTree> getTree() {
		return tree;
	}

	/**
	 * @param tree
	 * @return this tree
	 */
	public Tree setTree(List<CodeTree> tree) {
		this.tree = tree;
		return this;
	}

	/**
	 * @return sha
	 */
	public String getSha() {
		return sha;
	}

	/**
	 * @param sha
	 * @return this tree
	 */
	public Tree setSha(String sha) {
		this.sha = sha;
		return this;
	}
}
