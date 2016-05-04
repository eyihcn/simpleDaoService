package test;

import java.util.List;

public class Dept {

	private Integer deptNo;

	private List<Account> listacc;
	
	public List<Account> getListacc() {
		return listacc;
	}

	public void setListacc(List<Account> listacc) {
		this.listacc = listacc;
	}

	public Integer getDeptNo() {
		return deptNo;
	}

	public void setDeptNo(Integer deptNo) {
		this.deptNo = deptNo;
	}
	
}
