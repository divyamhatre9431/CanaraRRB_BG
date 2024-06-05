package com.idbi.intech.aml.LN;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class LexisNexisDao {
	private static Connection con_aml = null;

	static {
		try {
			con_aml = ConnectionFactory.makeConnectionAMLLive();
		} catch (Exception sqlExp) {
			sqlExp.printStackTrace();
			System.err.println("Exception e:" + sqlExp);
		}
	}

	public void commitdetails() {
		try {
			con_aml.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void truncateTables() {
		PreparedStatement pstmtEntity = null;
		try {
			System.out.println("Truncating all tables...");
			pstmtEntity = con_aml
					.prepareStatement("truncate table aml_ln_entities_tmp");
			pstmtEntity.execute();
			pstmtEntity = con_aml
					.prepareStatement("truncate table AML_LN_ENTITIESSOURCES_TMP");
			pstmtEntity.execute();
			pstmtEntity = con_aml
					.prepareStatement("truncate table AML_LN_ENTRYCATEGORY_TMP");
			pstmtEntity.execute();
			pstmtEntity = con_aml
					.prepareStatement("truncate table AML_LN_ENTITIESCOUNTRIES_TMP");
			pstmtEntity.execute();
			pstmtEntity = con_aml
					.prepareStatement("truncate table AML_LN_ENTITIESENTRYTYPE_TMP");
			pstmtEntity.execute();
			pstmtEntity = con_aml
					.prepareStatement("truncate table AML_LN_ENTITIESLEVELS_TMP");
			pstmtEntity.execute();
			pstmtEntity = con_aml
					.prepareStatement("truncate table AML_LN_ENTITIESSUBCATEGORY_TMP");
			pstmtEntity.execute();
			pstmtEntity = con_aml
					.prepareStatement("truncate table AML_LN_ENTITIESADDRESSES_TMP");
			pstmtEntity.execute();
			pstmtEntity = con_aml
					.prepareStatement("truncate table AML_LN_ENTITIESRELATIONSHIPS_T");
			pstmtEntity.execute();
			pstmtEntity = con_aml
					.prepareStatement("truncate table AML_LN_ENTITIESRELDEFS_TMP");
			pstmtEntity.execute();
			pstmtEntity = con_aml
					.prepareStatement("truncate table AML_LN_SANCTIONDOB_TMP");
			pstmtEntity.execute();
			pstmtEntity = con_aml
					.prepareStatement("truncate table AML_LN_ENTITIESDELETIONS_TMP");
			pstmtEntity.execute();

			System.out.println("Truncation Completed...");
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (pstmtEntity != null) {
					pstmtEntity.close();
					pstmtEntity = null;
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void updateLnDetails() {
		PreparedStatement pstmtEntity = null;
		try {
			System.out.println("Updating tables...");
			pstmtEntity = con_aml.prepareStatement("call update_ln_details()");
			pstmtEntity.execute();
			System.out.println("Updating Completed...");
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (pstmtEntity != null) {
					pstmtEntity.close();
					pstmtEntity = null;
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public int insertEntityDetails(Entities entities) {
		PreparedStatement pstmtEntity = null;
		//PreparedStatement pstmtGender = null;
		int updateCnt = 0;
		try {

			pstmtEntity = con_aml
					.prepareStatement("insert into aml_ln_entities_tmp(name,ent_id,firstname,lastname,prefix,suffix,aka,namesource,parentid,govdesignation,entrytype,entrycategory,entrysubcategory,organization,positions,remarks,dob,pob,country,expirationdate,effectivedate,picturefile,linkedto,related_id,sourceweblink,touchdate,directid,passportid,nationalid,otherid,dob2,entlevel,masterid,watch,relationships,primaryname,uploaddate,gender,scan_flg) "
							+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE,?,'N')");

			pstmtEntity.setString(1, entities.getName() != null ? entities
					.getName().toUpperCase() : null);
			pstmtEntity.setString(2, entities.getEnt_ID() != null ? entities
					.getEnt_ID().toUpperCase() : null);
			pstmtEntity.setString(3, entities.getFirstName() != null ? entities
					.getFirstName().toUpperCase() : null);
			pstmtEntity.setString(4, entities.getLastName() != null ? entities
					.getLastName().toUpperCase() : null);
			pstmtEntity.setString(5, entities.getPrefix() != null ? entities
					.getPrefix().toUpperCase() : null);
			pstmtEntity.setString(6, entities.getSuffix() != null ? entities
					.getSuffix().toUpperCase() : null);
			pstmtEntity.setString(7, entities.getAka() != null ? entities
					.getAka().toUpperCase() : null);
			pstmtEntity.setString(8,
					entities.getNameSource() != null ? entities.getNameSource()
							.toUpperCase() : null);
			pstmtEntity.setString(9, entities.getParentID() != null ? entities
					.getParentID().toUpperCase() : null);
			pstmtEntity.setString(10,
					entities.getGovDesignation() != null ? entities
							.getGovDesignation().toUpperCase() : null);
			pstmtEntity.setString(11,
					entities.getEntryType() != null ? entities.getEntryType()
							.toUpperCase() : null);
			pstmtEntity.setString(12,
					entities.getEntryCategory() != null ? entities
							.getEntryCategory().toUpperCase() : null);
			pstmtEntity.setString(13,
					entities.getEntrySubCategory() != null ? entities
							.getEntrySubCategory().toUpperCase() : null);
			pstmtEntity.setString(14, entities.getOrganization().toUpperCase());
			pstmtEntity.setString(15,
					entities.getPositions() != null ? entities.getPositions()
							.toUpperCase() : null);
			pstmtEntity.setString(16, entities.getRemarks() != null ? entities
					.getRemarks().toUpperCase() : null);
			pstmtEntity.setString(17, entities.getDOB() != null ? entities
					.getDOB().toUpperCase() : null);
			pstmtEntity.setString(18, entities.getPOB() != null ? entities
					.getPOB().toUpperCase() : entities.getPOB());
			pstmtEntity.setString(19, entities.getCountry() != null ? entities
					.getCountry().toUpperCase() : entities.getCountry());
			pstmtEntity.setString(
					20,
					entities.getExpirationDate() != null ? entities
							.getExpirationDate().toUpperCase() : entities
							.getExpirationDate());
			pstmtEntity.setString(21,
					entities.getEffectiveDate() != null ? entities
							.getEffectiveDate().toUpperCase() : null);
			pstmtEntity.setString(22,
					entities.getPictureFile() != null ? entities
							.getPictureFile().toUpperCase() : null);
			pstmtEntity.setString(23, entities.getLinkedTo() != null ? entities
					.getLinkedTo().toUpperCase() : null);
			pstmtEntity.setString(24,
					entities.getRelated_ID() != null ? entities.getRelated_ID()
							.toUpperCase() : null);
			pstmtEntity.setString(25,
					entities.getSourceWebLink() != null ? entities
							.getSourceWebLink().toUpperCase() : null);
			pstmtEntity.setString(26,
					entities.getTouchDate() != null ? entities.getTouchDate()
							.toUpperCase() : null);
			pstmtEntity.setString(27, entities.getDirectId() != null ? entities
					.getDirectId().toUpperCase() : null);
			pstmtEntity.setString(28,
					entities.getPassportID() != null ? entities.getPassportID()
							.toUpperCase() : null);
			pstmtEntity.setString(29,
					entities.getNationalID() != null ? entities.getNationalID()
							.toUpperCase() : null);
			pstmtEntity.setString(30, entities.getOtherID() != null ? entities
					.getOtherID().toUpperCase() : null);
			pstmtEntity.setString(31, entities.getDOB2() != null ? entities
					.getDOB2().toUpperCase() : null);
			pstmtEntity.setString(32, entities.getEntLevel() != null ? entities
					.getEntLevel().toUpperCase() : null);
			pstmtEntity.setString(33, entities.getMasterID() != null ? entities
					.getMasterID().toUpperCase() : null);
			pstmtEntity.setString(34, entities.getWatch() != null ? entities
					.getWatch().toUpperCase() : null);
			pstmtEntity.setString(35,
					entities.getRelationShips() != null ? entities
							.getRelationShips().toUpperCase() : null);
			pstmtEntity.setString(36,
					entities.getPrimaryName() != null ? entities
							.getPrimaryName().toUpperCase() : null);
			pstmtEntity.setString(37, entities.getGender() != null ? entities
					.getGender().toUpperCase() : null);
			updateCnt = pstmtEntity.executeUpdate();
			
//			pstmtGender = con_aml
//					.prepareStatement("insert into aml_ln_entitiesgender_tmp(ent_id,gender) values (?,?)");
//			pstmtGender.setString(1, entities.getEnt_ID() != null ? entities
//					.getEnt_ID().toUpperCase() : null);
//			pstmtGender.setString(2, entities.getGender() != null ? entities
//					.getGender().toUpperCase() : null);
//			updateCnt= pstmtGender.executeUpdate();

			// con_aml.commit();

		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (pstmtEntity != null) {
					pstmtEntity.close();
					pstmtEntity = null;
				}
				/*if (pstmtGender != null) {
					pstmtGender.close();
					pstmtGender = null;
				}*/

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return updateCnt;
	}

	public int insertEntitySources(EntitiesSources entitiesSources) {
		PreparedStatement pstmtEntitySources = null;
		int updateCnt = 0;
		try {

			pstmtEntitySources = con_aml
					.prepareStatement("insert into AML_LN_ENTITIESSOURCES_TMP(SOURCEID,COUNTRY,SOURCENAME,SOURCEABBREV) values (?,?,?,?)");
			pstmtEntitySources.setString(1, entitiesSources.getSourceID()
					.toUpperCase());
			pstmtEntitySources.setString(2, entitiesSources.getCountry()
					.toUpperCase());
			pstmtEntitySources.setString(3, entitiesSources.getSourceName()
					.toUpperCase());
			pstmtEntitySources.setString(4, entitiesSources.getSourceAbbrev()
					.toUpperCase());
			updateCnt = pstmtEntitySources.executeUpdate();
			// con_aml.commit();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmtEntitySources != null) {
					pstmtEntitySources.close();
					pstmtEntitySources = null;
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return updateCnt;
	}

	public int insertEntityCategories(EntitiesCategories entitiesCategory) {
		PreparedStatement pstmtEntityCategory = null;

		int updateCnt = 0;
		try {
			pstmtEntityCategory = con_aml
					.prepareStatement("insert into AML_LN_ENTRYCATEGORY_TMP(ID,ENTRYCATEGORY) values(?,?)");
			pstmtEntityCategory.setString(1, entitiesCategory.getID()
					.toUpperCase());
			pstmtEntityCategory.setString(2, entitiesCategory
					.getEntryCategory().toUpperCase());
			updateCnt = pstmtEntityCategory.executeUpdate();

			// con_aml.commit();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (pstmtEntityCategory != null) {
					pstmtEntityCategory.close();
					pstmtEntityCategory = null;
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return updateCnt;
	}

	public int insertEntityCountry(EntitiesCountries entitiesCountry) {
		PreparedStatement pstmtEntityCountry = null;

		int updateCnt = 0;

		try {

			pstmtEntityCountry = con_aml
					.prepareStatement("insert into AML_LN_ENTITIESCOUNTRIES_TMP(COUNTRYID,COUNTRYNAME) values(?,?)");
			pstmtEntityCountry.setString(1, entitiesCountry.getCountryID()
					.toUpperCase());
			pstmtEntityCountry.setString(2, entitiesCountry.getCountryName()
					.toUpperCase());
			updateCnt = pstmtEntityCountry.executeUpdate();

			// con_aml.commit();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmtEntityCountry != null) {
					pstmtEntityCountry.close();
					pstmtEntityCountry = null;
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return updateCnt;
	}

	public int insertEntityEntryTypes(EntitiesEntryTypes entitiesEntryTypes) {
		PreparedStatement pstmtEntityEntryTypes = null;

		int updateCnt = 0;
		try {

			pstmtEntityEntryTypes = con_aml
					.prepareStatement("insert into AML_LN_ENTITIESENTRYTYPE_TMP(ID,ENTRYDESC) values(?,?)");
			pstmtEntityEntryTypes.setString(1, entitiesEntryTypes.getID()
					.toUpperCase());
			pstmtEntityEntryTypes.setString(2, entitiesEntryTypes
					.getEntryDesc().toUpperCase());
			updateCnt = pstmtEntityEntryTypes.executeUpdate();
			// con_aml.commit();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmtEntityEntryTypes != null) {
					pstmtEntityEntryTypes.close();
					pstmtEntityEntryTypes = null;
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return updateCnt;
	}

	public int insertEntityLevels(EntitiesLevels entitiesLevels) {
		PreparedStatement pstmtEntityLevels = null;

		int updateCnt = 0;
		try {

			pstmtEntityLevels = con_aml
					.prepareStatement("insert into AML_LN_ENTITIESLEVELS_TMP(ID,LEVELDESC) values(?,?)");
			pstmtEntityLevels
					.setString(1, entitiesLevels.getID().toUpperCase());
			pstmtEntityLevels.setString(2, entitiesLevels.getLevelDesc()
					.toUpperCase());
			updateCnt = pstmtEntityLevels.executeUpdate();
			// con_aml.commit();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmtEntityLevels != null) {
					pstmtEntityLevels.close();
					pstmtEntityLevels = null;
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return updateCnt;
	}

	public int insertEntitySubcategory(EntitiesSubCategory entitiesSubCategory) {
		PreparedStatement pstmtEntitySubCategory = null;

		int updateCnt = 0;
		try {
			pstmtEntitySubCategory = con_aml
					.prepareStatement("insert into AML_LN_ENTITIESSUBCATEGORY_TMP(SUBCATID,SUBCATDESC) values(?,?)");
			pstmtEntitySubCategory.setString(1, entitiesSubCategory.getID()
					.toUpperCase());
			pstmtEntitySubCategory.setString(2, entitiesSubCategory
					.getEntrySubCategory().toUpperCase());
			updateCnt = pstmtEntitySubCategory.executeUpdate();
			// con_aml.commit();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmtEntitySubCategory != null) {
					pstmtEntitySubCategory.close();
					pstmtEntitySubCategory = null;
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return updateCnt;
	}

	public int insertEntityAddresses(EntitiesAddresses entityAddresses) {
		PreparedStatement pstmtEntityAddress = null;
		int updateCnt = 0;
		try {

			pstmtEntityAddress = con_aml
					.prepareStatement("insert into AML_LN_ENTITIESADDRESSES_TMP(ADDRESS_ID,ENT_ID,ADDRESS,CITY,STATEPROVINCE,COUNTRY,POSTALCODE,REMARKS,NAMESOURCE) values(?,?,?,?,?,?,?,?,?)");
			pstmtEntityAddress.setString(1, entityAddresses.getAddress_ID()
					.toUpperCase());
			pstmtEntityAddress.setString(2, entityAddresses.getEnt_ID()
					.toUpperCase());
			pstmtEntityAddress.setString(3,
					entityAddresses.getAddress() != null ? entityAddresses
							.getAddress().toUpperCase() : null);
			pstmtEntityAddress.setString(4,
					entityAddresses.getCity() != null ? entityAddresses
							.getCity().toUpperCase() : null);
			pstmtEntityAddress
					.setString(
							5,
							entityAddresses.getStateProvince() != null ? entityAddresses
									.getStateProvince().toUpperCase() : null);
			pstmtEntityAddress.setString(6,
					entityAddresses.getCountry() != null ? entityAddresses
							.getCountry().toUpperCase() : null);
			pstmtEntityAddress.setString(7,
					entityAddresses.getPostalCode() != null ? entityAddresses
							.getPostalCode().toUpperCase() : null);
			pstmtEntityAddress.setString(8, entityAddresses.getRemarks()
					.toUpperCase());
			pstmtEntityAddress.setString(9,
					entityAddresses.getNameSources() != null ? entityAddresses
							.getNameSources().toUpperCase() : null);

			updateCnt = pstmtEntityAddress.executeUpdate();
			// con_aml.commit();

		} catch (SQLIntegrityConstraintViolationException e) {

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmtEntityAddress != null) {
					pstmtEntityAddress.close();
					pstmtEntityAddress = null;
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return updateCnt;
	}

	public int insertEntityRelationships(
			EntitiesRelationships entitiesRelationships) {
		PreparedStatement pstmtEntityRelationShips = null;
		int updateCnt = 0;
		try {

			pstmtEntityRelationShips = con_aml
					.prepareStatement("insert into AML_LN_ENTITIESRELATIONSHIPS_T(RID,ENT_IDPARENT,ENT_IDCHILD,RELATIONID) values(?,?,?,?)");
			pstmtEntityRelationShips.setString(1, entitiesRelationships
					.getRID().toUpperCase());
			pstmtEntityRelationShips.setString(2, entitiesRelationships
					.getEnt_IDParent() != null ? entitiesRelationships
					.getEnt_IDParent().toUpperCase() : null);
			pstmtEntityRelationShips.setString(3, entitiesRelationships
					.getEnt_IDChild() != null ? entitiesRelationships
					.getEnt_IDChild().toUpperCase() : null);
			pstmtEntityRelationShips.setString(4, entitiesRelationships
					.getRelationID() != null ? entitiesRelationships
					.getRelationID().toUpperCase() : null);
			updateCnt = pstmtEntityRelationShips.executeUpdate();
			// con_aml.commit();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmtEntityRelationShips != null) {
					pstmtEntityRelationShips.close();
					pstmtEntityRelationShips = null;
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return updateCnt;
	}

	public int insertEntityRelDefs(EntitiesRelDefs entitiesRelDefs) {
		PreparedStatement pstmtEntityRelDefs = null;

		int updateCnt = 0;
		try {
			pstmtEntityRelDefs = con_aml
					.prepareStatement("insert into AML_LN_ENTITIESRELDEFS_TMP(RELATIONID,RELATIONDEF) values(?,?)");
			pstmtEntityRelDefs.setString(1,
					entitiesRelDefs.getRelationID() != null ? entitiesRelDefs
							.getRelationID().toUpperCase() : null);
			pstmtEntityRelDefs.setString(2,
					entitiesRelDefs.getRelationDef() != null ? entitiesRelDefs
							.getRelationDef().toUpperCase() : null);

			updateCnt = pstmtEntityRelDefs.executeUpdate();
			// con_aml.commit();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmtEntityRelDefs != null) {
					pstmtEntityRelDefs.close();
					pstmtEntityRelDefs = null;
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return updateCnt;
	}

	public int insertEntitySanctionDob(EntitiesSanctionDob entitiesSanctionDob) {
		PreparedStatement pstmtEntitySancDob = null;
		int updateCnt = 0;
		try {
			pstmtEntitySancDob = con_aml
					.prepareStatement("insert into AML_LN_SANCTIONDOB_TMP(SANCTIONDOB_ID,ENT_ID,DOB) values(?,?,?)");
			pstmtEntitySancDob.setString(1, entitiesSanctionDob
					.getSanctionsDobId() != null ? entitiesSanctionDob
					.getSanctionsDobId().toUpperCase() : null);
			pstmtEntitySancDob
					.setString(
							2,
							entitiesSanctionDob.getEnt_Id() != null ? entitiesSanctionDob
									.getEnt_Id().toUpperCase() : null);
			pstmtEntitySancDob.setString(3, entitiesSanctionDob.getDOB());

			updateCnt = pstmtEntitySancDob.executeUpdate();
			// con_aml.commit();

		} catch (SQLIntegrityConstraintViolationException e) {
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmtEntitySancDob != null) {
					pstmtEntitySancDob.close();
					pstmtEntitySancDob = null;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return updateCnt;
	}

	public int insertEntityDeletedEntries(EntitiesDeletions entitiesDeletion) {
		PreparedStatement pstmtEntityDeleted = null;
		int updateCnt = 0;
		try {
			pstmtEntityDeleted = con_aml
					.prepareStatement("insert into AML_LN_ENTITIESDELETIONS_TMP(ENT_ID,NAME,DATEDELETED) values(?,?,?)");
			pstmtEntityDeleted.setString(1,
					entitiesDeletion.getEnt_ID() != null ? entitiesDeletion
							.getEnt_ID().toUpperCase() : null);
			pstmtEntityDeleted.setString(2,
					entitiesDeletion.getName() != null ? entitiesDeletion
							.getName().toUpperCase() : null);
			pstmtEntityDeleted
					.setString(
							3,
							entitiesDeletion.getDateDeleted() != null ? entitiesDeletion
									.getDateDeleted().toUpperCase() : null);

			updateCnt = pstmtEntityDeleted.executeUpdate();
			// con_aml.commit();
		} catch (SQLIntegrityConstraintViolationException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmtEntityDeleted != null) {
					pstmtEntityDeleted.close();
					pstmtEntityDeleted = null;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return updateCnt;
	}

	public int insertRecordCnt(EntititesRecord entitiesRecord,
			String uploadedFile) {
		System.out.println("uploadedFile : "+uploadedFile);
		
		String filedt=uploadedFile.substring(uploadedFile.indexOf("_") + 1,
				uploadedFile.indexOf(".xml"));
		
		PreparedStatement pstmtRecord = null;
		int updateCnt = 0;
		try {
			pstmtRecord = con_aml
					.prepareStatement("insert into AML_LN_RECORD(ENTITIES,ENTITIESADDRESSES,ENTITIESRELATIONSHIPS,ENTITIESRELDEFS,ENTITIESSOURCES,ENTITIESCATEGORIES,ENTITIESENTRYTYPES,ENTITIESCOUNTRIES,ENTITIESSUBCATEGORIES,ENTITIESLEVELS,ENTITIESDELETIONS,SANCTIONSDOB,UPLOAD_DT,FILE_DT,FILE_NAME) values(?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE,?,?)");
			// System.out.println("ent Cnt: "+entitiesRecord.getEntitiesCount()+
			// "entAdd: "+entitiesRecord.getEntitiesAddress()+"entRel: "+entitiesRecord.getEntitiesRelationships()
			// +"entRelDel: "+entitiesRecord.getEntitiesRelDefs()+"entSource: "+entitiesRecord.getEntitiesSources()+""+entitiesRecord.getEntitiesCategory()+"entType: "+entitiesRecord.getEntitiesEntryType()
			// +"countries:"
			// +entitiesRecord.getEntitiesCountriesCnt()+" subCat:"+entitiesRecord.getEntitiesSubCategory()+" levelsCnt : "+entitiesRecord.getEntitiesLevelsCnt()+" deletions: "+entitiesRecord.getEntitiesDeletions()+" sanDOB :"+entitiesRecord.getEntitiesSanctionDob());
			pstmtRecord.setInt(1, entitiesRecord.getEntitiesCount());
			pstmtRecord.setInt(2, entitiesRecord.getEntitiesAddress());
			pstmtRecord.setInt(3,
					entitiesRecord.getEntitiesRelationships());
			pstmtRecord.setInt(4, entitiesRecord.getEntitiesRelDefs());
			pstmtRecord.setInt(5, entitiesRecord.getEntitiesSources());
			pstmtRecord.setInt(6, entitiesRecord.getEntitiesCategory());
			pstmtRecord
					.setInt(7, entitiesRecord.getEntitiesEntryType());
			pstmtRecord.setInt(8,
					entitiesRecord.getEntitiesCountriesCnt());
			pstmtRecord.setInt(9,
					entitiesRecord.getEntitiesSubCategory());
			pstmtRecord.setInt(10,
					entitiesRecord.getEntitiesLevelsCnt());
			pstmtRecord.setInt(11,
					entitiesRecord.getEntitiesDeletions());
			pstmtRecord.setInt(12,
					entitiesRecord.getEntitiesSanctionDob());
			pstmtRecord.setString(13, filedt);
			pstmtRecord.setString(14, uploadedFile);

			updateCnt = pstmtRecord.executeUpdate();
			con_aml.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmtRecord != null) {
					pstmtRecord.close();
					pstmtRecord = null;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return updateCnt;
	}

}
