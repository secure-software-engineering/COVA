/**
 * Copyright (C) 2019 Linghui Luo 
 * 
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package cova.data;

import java.util.ArrayList;
import java.util.List;

/**
 * The witness path is represented by a set of code positions.
 */
public class WitnessPath {

	/**
	 * Each position is encoded as a String inform NodeType@ClassName@LineNumber
	 */
	private List<String> positions;

	public WitnessPath() {
		this.positions = new ArrayList<String>();
	}

	public void add(NodeType type, String className, int lineNumber) {
		StringBuilder b = new StringBuilder(type.toString());
		b.append("@");
		b.append(className);
		b.append("@");
		b.append(lineNumber);
		String str = b.toString();
		if (!this.positions.contains(str))
			this.positions.add(b.toString());
	}

	public static String generatePositionString(NodeType type, String className, int lineNumber) {
		StringBuilder b = new StringBuilder(type.toString());
		b.append("@");
		b.append(className);
		b.append("@");
		b.append(lineNumber);
		return b.toString();
	}

	public void add(String position) {
		if (position != null && !this.positions.contains(position)) {
			this.positions.add(position);
		}
	}

	public void addAll(List<String> positions) {
		for (String position : positions)
			this.add(position);
	}

	public static WitnessPath merge(WitnessPath p1, WitnessPath p2) {
		WitnessPath p = new WitnessPath();
		for (String s : p1.positions)
			if (!p.positions.contains(s))
				p.positions.add(s);
		for (String s : p2.positions)
			if (!p.positions.contains(s))
				p.positions.add(s);
		return p;
	}

	public static WitnessPath copy(WitnessPath p) {
		WitnessPath r = new WitnessPath();
		r.positions.addAll(p.positions);
		return r;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder("Path: ");
		for (int i = 0; i < this.positions.size(); i++) {
			b.append(this.positions.get(i));
			if (i != this.positions.size() - 1)
				b.append("\n\t->");
		}
		return b.toString();
	}

	public List<String> getPositions() {
		return this.positions;
	}
}
