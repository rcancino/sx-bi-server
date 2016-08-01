package sx.bi

/**
 * Created by rcancino on 23/06/16.
 */
enum Mes {
	ENE(1),
	FEB(2),
	MAR(3),
	ABR(4),
	MAY(5),
	JUN(6),
	JUL(7),
	AGO(8),
	SEP(9),
	OCT(10),
	NOV(11),
	DIC(12)

	final int id

	private Mes(int id){
		this.id = id
	}

	public String value() {
		return name();
	}

	public int getId(){
		return id;
	}


	static Map mesesMap = [1:ENE, 2:FEB, 3:MAR, 4:ABR, 5:MAY, 6:JUN, 7:JUL, 8:AGO, 9:SEP, 10:OCT, 11:NOV, 12:DIC]

	public static Mes fromValue(Integer v){
		if(v < 1 || v > 12)
			throw new IllegalArgumentException("El numero de mes debe ser 1 a 12 ")
		return mesesMap[v]
	}


}