package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorkPieceList {

	public static List<WorkPiece> list = Collections.synchronizedList(new ArrayList<WorkPiece>());
	
}
