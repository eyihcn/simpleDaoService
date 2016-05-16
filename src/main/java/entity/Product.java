package entity;

public class Product extends BaseEntity<Long> {

	private static final long serialVersionUID = -4500237646756252887L;
	private Long id;
	private String name;
	private double unitPrice;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", unitPrice="
				+ unitPrice + "]";
	}

}
