package com.sapient.dev.core.dto;

/**
 * Class for the Product DTO
 */
public class ProductDTO {

    private String id;
    private String title;

    public ProductDTO() {
        super();
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}