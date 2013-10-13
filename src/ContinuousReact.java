
import fr.cyann.functor.Procedure1;
import fr.cyann.react.MouseReact;
import fr.cyann.react.TimeReact;

/*
 * Copyright (C) 2013 Yann Caron
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Less General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Less General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
public class ContinuousReact {

	/**
	 * Main methode, programm entry point.
	 * @param args the command line arguments.
	 */
	public static void main(String[] args) {
		
		final long start = System.currentTimeMillis();
		
		TimeReact.once(50L).mergeHeterogenous(MouseReact.once(150L)).subscribe(new Procedure1() {

			@Override
			public void invoke(Object arg1) {
				System.out.println("Time " + (System.currentTimeMillis() - start));
				System.out.println(arg1);
			}
		});
		
	}
}
