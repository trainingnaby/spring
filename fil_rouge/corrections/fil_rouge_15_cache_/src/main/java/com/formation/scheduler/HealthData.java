package com.formation.scheduler;

public class HealthData {
	private String status;
	Components ComponentsObject;

	// Getter Methods

	public String getStatus() {
		return status;
	}

	public Components getComponents() {
		return ComponentsObject;
	}

	// Setter Methods

	public void setStatus(String status) {
		this.status = status;
	}

	public void setComponents(Components componentsObject) {
		this.ComponentsObject = componentsObject;
	}
}

class Components {
	Db DbObject;
	DiskSpace DiskSpaceObject;
	DuplicataProbe DuplicataProbeObject;
	Ping PingObject;

	// Getter Methods

	public Db getDb() {
		return DbObject;
	}

	public DiskSpace getDiskSpace() {
		return DiskSpaceObject;
	}

	public DuplicataProbe getDuplicataProbe() {
		return DuplicataProbeObject;
	}

	public Ping getPing() {
		return PingObject;
	}

	// Setter Methods

	public void setDb(Db dbObject) {
		this.DbObject = dbObject;
	}

	public void setDiskSpace(DiskSpace diskSpaceObject) {
		this.DiskSpaceObject = diskSpaceObject;
	}

	public void setDuplicataProbe(DuplicataProbe duplicataProbeObject) {
		this.DuplicataProbeObject = duplicataProbeObject;
	}

	public void setPing(Ping pingObject) {
		this.PingObject = pingObject;
	}
}

class Ping {
	private String status;

	// Getter Methods

	public String getStatus() {
		return status;
	}

	// Setter Methods

	public void setStatus(String status) {
		this.status = status;
	}
}

class DuplicataProbe {
	private String status;
	DetailsDuplicata DetailsObject;

	// Getter Methods

	public String getStatus() {
		return status;
	}

	public DetailsDuplicata getDetails() {
		return DetailsObject;
	}

	// Setter Methods

	public void setStatus(String status) {
		this.status = status;
	}

	public void setDetails(DetailsDuplicata detailsObject) {
		this.DetailsObject = detailsObject;
	}
}

class DetailsDuplicata {
	CheckTotalDuplicatas CheckTotalDuplicatasObject;

	// Getter Methods

	public CheckTotalDuplicatas getCheckTotalDuplicatas() {
		return CheckTotalDuplicatasObject;
	}

	// Setter Methods

	public void setCheckTotalDuplicatas(CheckTotalDuplicatas CheckTotalDuplicatasObject) {
		this.CheckTotalDuplicatasObject = CheckTotalDuplicatasObject;
	}
}

class CheckTotalDuplicatas {
	private String TotalDuplicatasOk;

	// Getter Methods

	public String getTotalDuplicatasOk() {
		return TotalDuplicatasOk;
	}

	// Setter Methods

	public void setTotalDuplicatasOk(String TotalDuplicatasOk) {
		this.TotalDuplicatasOk = TotalDuplicatasOk;
	}
}

class DiskSpace {
	private String status;
	DetailsDiskSpace DetailsObject;

	// Getter Methods

	public String getStatus() {
		return status;
	}

	public DetailsDiskSpace getDetails() {
		return DetailsObject;
	}

	// Setter Methods

	public void setStatus(String status) {
		this.status = status;
	}

	public void setDetails(DetailsDiskSpace detailsObject) {
		this.DetailsObject = detailsObject;
	}
}

class DetailsDiskSpace {
	private float total;
	private float free;
	private float threshold;
	private String path;
	private boolean exists;

	// Getter Methods

	public float getTotal() {
		return total;
	}

	public float getFree() {
		return free;
	}

	public float getThreshold() {
		return threshold;
	}

	public String getPath() {
		return path;
	}

	public boolean getExists() {
		return exists;
	}

	// Setter Methods

	public void setTotal(float total) {
		this.total = total;
	}

	public void setFree(float free) {
		this.free = free;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setExists(boolean exists) {
		this.exists = exists;
	}
}

class Db {
	private String status;
	Details DetailsObject;

	// Getter Methods

	public String getStatus() {
		return status;
	}

	public Details getDetails() {
		return DetailsObject;
	}

	// Setter Methods

	public void setStatus(String status) {
		this.status = status;
	}

	public void setDetails(Details detailsObject) {
		this.DetailsObject = detailsObject;
	}
}

class Details {
	private String database;
	private String validationQuery;

	// Getter Methods

	public String getDatabase() {
		return database;
	}

	public String getValidationQuery() {
		return validationQuery;
	}

	// Setter Methods

	public void setDatabase(String database) {
		this.database = database;
	}

	public void setValidationQuery(String validationQuery) {
		this.validationQuery = validationQuery;
	}
}