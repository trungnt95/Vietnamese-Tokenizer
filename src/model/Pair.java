/**
 * 
 */
package model;

/**
 * @author Nguyen
 *
 */
public class Pair<L, R> {
	private final L first;
	private final R second;

	public Pair(L first, R second) {
		this.first = first;
		this.second = second;
	}

	public L getFirst() {
		return first;
	}

	public R getSecond() {
		return second;
	}

	/*@Override
	public int hashCode() {
		return first.hashCode() ^ second.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Pair))
			return false;
		Pair pair = (Pair) obj;
		return this.first.equals(pair.getFirst()) 
				&& this.second.equals(pair.getSecond());
	}*/

}
