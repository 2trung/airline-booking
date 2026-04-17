package org.opencps.api.controller.impl;

import backend.auth.api.exception.BusinessExceptionImpl;
import backend.auth.api.exception.NotFoundException;
import backend.auth.api.exception.UnauthorizationException;
import backend.utils.APIDateTimeUtils;
import backend.utils.ObjectConverterUtil;
import com.google.gson.Gson;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.*;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.*;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.http.HttpStatus;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.opencps.api.constants.ConstantUtils;
import org.opencps.api.constants.StatisticFilestoreGovDTO;
import org.opencps.api.constants.StatisticManagementConstants;
import org.opencps.api.controller.FileStoreGovManagement;
import org.opencps.api.controller.util.*;
import org.opencps.api.controller.validator.FileStoreFolderValidator;
import org.opencps.api.controller.validator.FileStoreGovValidator;
import org.opencps.api.dossierfile.model.FileStoreGovInputModel;
import org.opencps.api.error.model.ErrorMsg;
import org.opencps.api.fileitemmgt.model.FileStoreGovResponse;
import org.opencps.api.fileitemmgt.model.FileStoreGovSearchModel;
import org.opencps.api.fileitemmgt.model.SuggestDossierNoResponse;
import org.opencps.auth.api.BackendAuth;
import org.opencps.auth.api.BackendAuthImpl;
import org.opencps.datamgt.constants.DataMGTConstants;
import org.opencps.datamgt.constants.DictItemTerm;
import org.opencps.datamgt.model.DictCollection;
import org.opencps.datamgt.model.DictItem;
import org.opencps.datamgt.model.OtherDictItem;
import org.opencps.datamgt.service.DictCollectionLocalServiceUtil;
import org.opencps.datamgt.service.DictItemLocalServiceUtil;
import org.opencps.datamgt.service.OtherDictItemLocalServiceUtil;
import org.opencps.datamgt.utils.DictCollectionUtils;
import org.opencps.dossiermgt.action.FileUploadUtils;
import org.opencps.dossiermgt.action.util.SpecialCharacterUtils;
import org.opencps.dossiermgt.constants.*;
import org.opencps.dossiermgt.model.*;
import org.opencps.dossiermgt.qlcd.model.QLCDResponse;
import org.opencps.dossiermgt.rest.model.FileStoreGovRequest;
import org.opencps.dossiermgt.service.*;
import org.opencps.dossiermgt.service.ServiceInfoLocalServiceUtil;
import org.opencps.kernel.util.DateTimeUtil;
import org.opencps.usermgt.model.Employee;
import org.opencps.usermgt.service.EmployeeLocalServiceUtil;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.io.File;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.sql.*;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FileStoreGovManagementImpl implements FileStoreGovManagement {
    Log _log = LogFactoryUtil.getLog(DossierManagementImpl.class);
    public static final String GOVERNMENT_AGENCY = "GOVERNMENT_AGENCY";
    public static final String _ZERO_STR = "0";
    public static final long MCDT_GROUP_ID = 272638;

    public static String convertNormalDateToLuceneDate(String normal) {
        if (Validator.isNull(normal)) {
            return StringPool.BLANK;
        }
        String[] splitD = normal.split(StringPool.FORWARD_SLASH);
        if (splitD.length != 3 ||
                splitD[1].length() > 2 ||
                splitD[0].length() > 2) {
            return StringPool.BLANK;
        }
        String year = splitD[2];
        String month = (splitD[1].length() == 1) ? _ZERO_STR + splitD[1] : splitD[1];
        String day = (splitD[0].length() == 1) ? _ZERO_STR + splitD[0] : splitD[0];

        return year + month + day;
    }

    private String getScope(FileStoreGovSearchModel searchModel, User user, long groupId) {
        String govAgencyCode = null;
        Employee employee = EmployeeLocalServiceUtil.fetchByFB_MUID(user.getUserId());
        if (Validator.isNotNull(searchModel.getGovAgencyName()) && !Validator.isNotNull(searchModel.getGovAgencyCode())) {
            govAgencyCode = getItemCodeByName(searchModel.getGovAgencyName().trim());
        }

        if (Validator.isNotNull(govAgencyCode)) {
            if (Validator.isNull(searchModel.getSiblingSearch()) || !searchModel.getSiblingSearch()) {
                return govAgencyCode;
            } else {
                String[] agencyList = govAgencyCode.split(",");
                StringBuilder scopeList = new StringBuilder();

                for (int i = 0; i < agencyList.length; i++) {
                    DictCollection dictCollection = DictCollectionLocalServiceUtil.fetchByF_dictCollectionCode(GOVERNMENT_AGENCY, groupId);
                    if (Validator.isNotNull(dictCollection)) {
                        DictItem dictItem = DictItemLocalServiceUtil.fetchByF_dictItemCode(agencyList[i], dictCollection.getDictCollectionId(), groupId);
                        scopeList.append(dictItem.getItemCode());
                    }
                }
                return scopeList.toString();
            }
        }
        if (Validator.isNotNull(employee)) {
            if (Validator.isNotNull(employee.getScope())) {
                DictCollection dictCollection = DictCollectionLocalServiceUtil.fetchByF_dictCollectionCode(GOVERNMENT_AGENCY, groupId);
                if (Validator.isNotNull(dictCollection)) {
                    DictItem dictItem = DictItemLocalServiceUtil.fetchByF_dictItemCode(employee.getScope(), dictCollection.getDictCollectionId(), groupId);
                    String scopeList = null;
                    if (Validator.isNotNull(dictItem)) {
                        scopeList = ScopeUtils.getAllSubUnit(dictItem, dictCollection.getDictCollectionId(), groupId);
                    }
                    if (scopeList != null) {
                        return scopeList;
                    } else {
                        return "";
                    }
                }
            }
        }
        return govAgencyCode;
    }

    public Map<String, Integer> countDossierNo(FileStoreGovSearchModel searchModel, List<Object> params, User user, long groupId, String scope) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(DISTINCT f.DOSSIERNO) AS TOTAL_DOSSIERNO FROM FILESTOREGOV f ");

        if (notEmpty(searchModel.getDomainCode())) {
            sql.append(" join OPENCPS_SERVICEINFO s on f.SERVICECODE = s.SERVICECODE ");
        }

        sql.append("WHERE f.GOVAGENCYCODE IS NOT NULL AND f.GROUPID = 272638 AND f.CREATEDATE is not null ");


        // isActive
        if (notEmpty(searchModel.getIsActive())) {
            sql.append(" AND f.ISACTIVE = ?");
            params.add(searchModel.getIsActive());
        } else {
            sql.append(" AND f.ISACTIVE = ?");
            params.add(1);
        }

        // partType
        if (notEmpty(searchModel.getPartType())) {
            sql.append(" AND f.PARTTYPE = ?");
            params.add(searchModel.getPartType());
        }

        // serviceCode
        if (notEmpty(searchModel.getServiceCode())) {
            sql.append(" AND f.SERVICECODE = ?");
            params.add(searchModel.getServiceCode());
        }

        // departmentIssue
        if (notEmpty(searchModel.getDepartmentIssue())) {
            sql.append(" AND LOWER(f.DEPARTMENTISSUE) LIKE ?");
            params.add("%" + searchModel.getDepartmentIssue().toLowerCase() + "%");
        }

        // folderId
        if (searchModel.getFolderId() != null) {
            sql.append(" AND f.FILESTOREGOVID in (select ff.FILESTOREID from OPENCPS_FILEFOLDER ff where ff.FILEFOLDERID = ?)");
            params.add(searchModel.getFolderId());
        }

        // ownerNo
        if (notEmpty(searchModel.getOwnerNo())) {
            sql.append(" AND f.OWNERNO like ?");
            params.add("%" + searchModel.getOwnerNo() + "%");
        }

        // ownerName
        if (notEmpty(searchModel.getOwnerName())) {
            sql.append(" AND LOWER(f.OWNERNAME) LIKE ?");
            params.add("%" + searchModel.getOwnerName().toLowerCase() + "%");
        }

        // fileName
        if (notEmpty(searchModel.getFileName())) {
            sql.append(" AND LOWER(f.FILENAME) LIKE ?");
            params.add("%" + searchModel.getFileName().toLowerCase() + "%");
        }

        // displayName
        if (notEmpty(searchModel.getDisplayName())) {
            sql.append(" AND LOWER(f.DISPLAYNAME) LIKE ?");
            params.add("%" + searchModel.getDisplayName().toLowerCase() + "%");
        }

        // codeNumber
        if (notEmpty(searchModel.getCodeNumber())) {
            sql.append(" AND f.CODENUMBER like ?");
            params.add("%" + searchModel.getCodeNumber() + "%");
        }

        // codeNotation
        if (notEmpty(searchModel.getCodeNotation())) {
            sql.append(" AND f.CODENOTATION like ?");
            params.add("%" + searchModel.getCodeNotation() + "%");
        }

        // fullInfo
        if (notEmpty(searchModel.getFullInfo())) {
            sql.append(" AND LOWER(f.FULLINFO) LIKE ?");
            params.add("%" + searchModel.getFullInfo().toLowerCase() + "%");
        }

        // dossierNo
        if (notEmpty(searchModel.getDossierNo())) {
            sql.append(" AND f.DOSSIERNO like ?");
            params.add("%" + searchModel.getDossierNo() + "%");
        }

        // dossierName
        if (notEmpty(searchModel.getDossierName())) {
            sql.append(" AND LOWER(f.DOSSIERNAME) LIKE ?");
            params.add("%" + searchModel.getDossierName().toLowerCase() + "%");
        }

        // abstractss
        if (notEmpty(searchModel.getAbstractss())) {
            sql.append(" AND LOWER(f.ABSTRACTSS) LIKE ?");
            params.add("%" + searchModel.getAbstractss().toLowerCase() + "%");
        }

        // combinedCode
        if (notEmpty(searchModel.getCombinedCode())) {
            sql.append(" AND (f.CODENUMBER || f.CODENOTATION) like ?");
            params.add("%" + searchModel.getCombinedCode() + "%");
        }

        // keyword (áp dụng cho nhiều cột)
        if (notEmpty(searchModel.getKeyword())) {
            String kw = "%" + searchModel.getKeyword().toLowerCase() + "%";
            sql.append(" AND (LOWER(f.FILENAME) LIKE ? OR LOWER(f.DISPLAYNAME) LIKE ? OR LOWER(f.DOSSIERNO) LIKE ? OR LOWER(f.DOSSIERNAME) LIKE ? OR LOWER(f.SERVICECODE) LIKE ?)");
            params.add(kw);
            params.add(kw);
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        // fromDate - toDate (ngày tạo)
        if (notEmpty(searchModel.getFromDate()) && notEmpty(searchModel.getToDate())) {
            sql.append(" AND f.CREATEDATE BETWEEN TO_DATE(?, 'DD/MM/YYYY') AND TO_DATE(?, 'DD/MM/YYYY')");
            params.add(searchModel.getFromDate());
            params.add(searchModel.getToDate());
        }

        // issueDateFrom - issueDateTo
        if (notEmpty(searchModel.getIssueDateFrom()) && notEmpty(searchModel.getIssueDateTo())) {
            sql.append(" AND f.ISSUEDATE BETWEEN TO_DATE(?, 'DD/MM/YYYY') AND TO_DATE(?, 'DD/MM/YYYY')");
            params.add(searchModel.getIssueDateFrom());
            params.add(searchModel.getIssueDateTo());
        }

        // DomainCode
        if (notEmpty(searchModel.getDomainCode())) {
            sql.append(" AND s.DOMAINCODE = ?");
            params.add(searchModel.getDomainCode());
        }

        int batchSize = 500;
        Map<String, Integer> resultMap = new HashMap<>();
        if (scope != null && !notEmpty(searchModel.getGovAgencyCode())) {
            // Tách danh sách GOVAGENCYCODE
            List<String> govCodes = Arrays.stream(scope.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty()).distinct()
                    .collect(Collectors.toList());

            // Chia nhỏ từng batch và query
            for (int i = 0; i < govCodes.size(); i += batchSize) {
                int end = Math.min(i + batchSize, govCodes.size());
                List<String> batch = govCodes.subList(i, end);

                // Tạo inClause có dấu nháy đơn
                String inClause = batch.stream()
                        .map(code -> "'" + code + "'")
                        .collect(Collectors.joining(","));

                String sqlWithInClause = sql + " AND f.GOVAGENCYCODE IN (" + inClause + ") ";
                queryAndAppendDossierNoCount(sqlWithInClause.toString(), resultMap, params);
            }
        } else {
            if (searchModel.getGovAgencyCode() != null &&  !searchModel.getGovAgencyCode().isEmpty()) {
                Boolean isSiblingSearch = searchModel.getSiblingSearch();
                if (Validator.isNull(isSiblingSearch) || !isSiblingSearch) {
                    sql.append(" AND f.GOVAGENCYCODE = ? ");
                    params.add(searchModel.getGovAgencyCode());
                } else {
                    DictCollection dictCollection = DictCollectionLocalServiceUtil.fetchByF_dictCollectionCode(GOVERNMENT_AGENCY, groupId);
                    if (Validator.isNotNull(dictCollection)) {
                        DictItem dictItem = DictItemLocalServiceUtil.fetchByF_dictItemCode(searchModel.getGovAgencyCode(), dictCollection.getDictCollectionId(), groupId);
                        String scopeList = null;
                        if (Validator.isNotNull(dictItem)) {
                            scopeList = ScopeUtils.getAllSubUnit(dictItem, dictCollection.getDictCollectionId(), groupId);
                        }
                        if (scopeList != null) {
                            List<String> scopeItems = Arrays.asList(scopeList.split(StringPool.COMMA));
                            String placeholders = scopeItems.stream().map(item -> "?").collect(Collectors.joining(","));
                            sql.append(" AND f.GOVAGENCYCODE IN (").append(placeholders).append(") ");

                            for (String code : scopeItems) {
                                params.add(code.trim());
                            }
                        } else {
                            sql.append(" AND f.GOVAGENCYCODE = ? ");
                            params.add(searchModel.getGovAgencyCode());
                        }
                    }
                }
            }
            queryAndAppendDossierNoCount(sql.toString(), resultMap, params);
        }
        return resultMap;
    }


    public Map<String, Integer> buildCountQueryForUsage(FileStoreGovSearchModel searchModel, List<Object> params, User user, long groupId, String scope) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(1) AS USAGE_COUNT " +
                "FROM FILESTOREGOV f left join FILEGOVUSEDHISTORY h on f.FILESTOREGOVID = h.FILESTOREGOVID ");

        if (notEmpty(searchModel.getDomainCode())) {
            sql.append(" join OPENCPS_SERVICEINFO s on f.SERVICECODE = s.SERVICECODE ");
        }

        sql.append("WHERE h.ACTIONSS = 'Tái sử dụng' AND f.GOVAGENCYCODE IS NOT NULL AND f.GROUPID = 272638 AND f.CREATEDATE is not null ");

        // isActive
        if (notEmpty(searchModel.getIsActive())) {
            sql.append(" AND f.ISACTIVE = ?");
            params.add(searchModel.getIsActive());
        } else {
            sql.append(" AND f.ISACTIVE = ?");
            params.add(1);
        }

        // partType
        if (notEmpty(searchModel.getPartType())) {
            sql.append(" AND f.PARTTYPE = ?");
            params.add(searchModel.getPartType());
        }

        // serviceCode
        if (notEmpty(searchModel.getServiceCode())) {
            sql.append(" AND f.SERVICECODE = ?");
            params.add(searchModel.getServiceCode());
        }

        // departmentIssue
        if (notEmpty(searchModel.getDepartmentIssue())) {
            sql.append(" AND LOWER(f.DEPARTMENTISSUE) LIKE ?");
            params.add("%" + searchModel.getDepartmentIssue().toLowerCase() + "%");
        }

        // folderId
        if (searchModel.getFolderId() != null) {
            sql.append(" AND f.FILESTOREGOVID in (select ff.FILESTOREID from OPENCPS_FILEFOLDER ff where ff.FILEFOLDERID = ?)");
            params.add(searchModel.getFolderId());
        }

        // ownerNo
        if (notEmpty(searchModel.getOwnerNo())) {
            sql.append(" AND f.OWNERNO like ?");
            params.add("%" + searchModel.getOwnerNo() + "%");
        }

        // ownerName
        if (notEmpty(searchModel.getOwnerName())) {
            sql.append(" AND LOWER(f.OWNERNAME) LIKE ?");
            params.add("%" + searchModel.getOwnerName().toLowerCase() + "%");
        }

        // fileName
        if (notEmpty(searchModel.getFileName())) {
            sql.append(" AND LOWER(f.FILENAME) LIKE ?");
            params.add("%" + searchModel.getFileName().toLowerCase() + "%");
        }

        // displayName
        if (notEmpty(searchModel.getDisplayName())) {
            sql.append(" AND LOWER(f.DISPLAYNAME) LIKE ?");
            params.add("%" + searchModel.getDisplayName().toLowerCase() + "%");
        }

        // codeNumber
        if (notEmpty(searchModel.getCodeNumber())) {
            sql.append(" AND f.CODENUMBER like ?");
            params.add("%" + searchModel.getCodeNumber() + "%");
        }

        // codeNotation
        if (notEmpty(searchModel.getCodeNotation())) {
            sql.append(" AND f.CODENOTATION like ?");
            params.add("%" + searchModel.getCodeNotation() + "%");
        }

        // fullInfo
        if (notEmpty(searchModel.getFullInfo())) {
            sql.append(" AND LOWER(f.FULLINFO) LIKE ?");
            params.add("%" + searchModel.getFullInfo().toLowerCase() + "%");
        }

        // dossierNo
        if (notEmpty(searchModel.getDossierNo())) {
            sql.append(" AND f.DOSSIERNO like ?");
            params.add("%" + searchModel.getDossierNo() + "%");
        }

        // dossierName
        if (notEmpty(searchModel.getDossierName())) {
            sql.append(" AND LOWER(f.DOSSIERNAME) LIKE ?");
            params.add("%" + searchModel.getDossierName().toLowerCase() + "%");
        }

        // abstractss
        if (notEmpty(searchModel.getAbstractss())) {
            sql.append(" AND LOWER(f.ABSTRACTSS) LIKE ?");
            params.add("%" + searchModel.getAbstractss().toLowerCase() + "%");
        }

        // combinedCode
        if (notEmpty(searchModel.getCombinedCode())) {
            sql.append(" AND (f.CODENUMBER || f.CODENOTATION) like ?");
            params.add("%" + searchModel.getCombinedCode() + "%");
        }

        // keyword (áp dụng cho nhiều cột)
        if (notEmpty(searchModel.getKeyword())) {
            String kw = "%" + searchModel.getKeyword().toLowerCase() + "%";
            sql.append(" AND (LOWER(f.FILENAME) LIKE ? OR LOWER(f.DISPLAYNAME) LIKE ? OR LOWER(f.DOSSIERNO) LIKE ? OR LOWER(f.DOSSIERNAME) LIKE ? OR LOWER(f.SERVICECODE) LIKE ?)");
            params.add(kw);
            params.add(kw);
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        // fromDate - toDate (ngày tạo)
        if (notEmpty(searchModel.getFromDate()) && notEmpty(searchModel.getToDate())) {
            sql.append(" AND f.CREATEDATE BETWEEN TO_DATE(?, 'DD/MM/YYYY') AND TO_DATE(?, 'DD/MM/YYYY')");
            params.add(searchModel.getFromDate());
            params.add(searchModel.getToDate());
        }

        // issueDateFrom - issueDateTo
        if (notEmpty(searchModel.getIssueDateFrom()) && notEmpty(searchModel.getIssueDateTo())) {
            sql.append(" AND f.ISSUEDATE BETWEEN TO_DATE(?, 'DD/MM/YYYY') AND TO_DATE(?, 'DD/MM/YYYY')");
            params.add(searchModel.getIssueDateFrom());
            params.add(searchModel.getIssueDateTo());
        }

        // DomainCode
        if (notEmpty(searchModel.getDomainCode())) {
            sql.append(" AND s.DOMAINCODE = ?");
            params.add(searchModel.getDomainCode());
        }

        int batchSize = 500;
        Map<String, Integer> resultMap = new HashMap<>();
        if (scope != null && !notEmpty(searchModel.getGovAgencyCode())) {
            // Tách danh sách GOVAGENCYCODE
            List<String> govCodes = Arrays.stream(scope.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty()).distinct()
                    .collect(Collectors.toList());

            // Chia nhỏ từng batch và query
            for (int i = 0; i < govCodes.size(); i += batchSize) {
                int end = Math.min(i + batchSize, govCodes.size());
                List<String> batch = govCodes.subList(i, end);

                // Tạo inClause có dấu nháy đơn
                String inClause = batch.stream()
                        .map(code -> "'" + code + "'")
                        .collect(Collectors.joining(","));

                String sqlWithInClause = sql + " AND f.GOVAGENCYCODE IN (" + inClause + ") ";
                queryAndAppendUsageCount(sqlWithInClause.toString(), resultMap, params);
            }
        } else {
            if (!searchModel.getGovAgencyCode().isEmpty()) {
                Boolean isSiblingSearch = searchModel.getSiblingSearch();
                if (Validator.isNull(isSiblingSearch) || !isSiblingSearch) {
                    sql.append(" AND f.GOVAGENCYCODE = ? ");
                    params.add(searchModel.getGovAgencyCode());
                } else {
                    DictCollection dictCollection = DictCollectionLocalServiceUtil.fetchByF_dictCollectionCode(GOVERNMENT_AGENCY, groupId);
                    if (Validator.isNotNull(dictCollection)) {
                        DictItem dictItem = DictItemLocalServiceUtil.fetchByF_dictItemCode(searchModel.getGovAgencyCode(), dictCollection.getDictCollectionId(), groupId);
                        String scopeList = null;
                        if (Validator.isNotNull(dictItem)) {
                            scopeList = ScopeUtils.getAllSubUnit(dictItem, dictCollection.getDictCollectionId(), groupId);
                        }
                        if (scopeList != null) {
                            List<String> scopeItems = Arrays.asList(scopeList.split(StringPool.COMMA));
                            String placeholders = scopeItems.stream().map(item -> "?").collect(Collectors.joining(","));
                            sql.append(" AND f.GOVAGENCYCODE IN (").append(placeholders).append(") ");

                            for (String code : scopeItems) {
                                params.add(code.trim());
                            }
                        } else {
                            sql.append(" AND f.GOVAGENCYCODE = ? ");
                            params.add(searchModel.getGovAgencyCode());
                        }
                    }
                }
            }
            queryAndAppendUsageCount(sql.toString(), resultMap, params);
        }
        return resultMap;
    }

    private static boolean notEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private int getDossierNoCount(FileStoreGovSearchModel fileStoreGovSearchModel, User user, long groupId) {
        List<Object> params = new ArrayList<>();
        String scope = getScope(fileStoreGovSearchModel, user, groupId);
        return countDossierNo(fileStoreGovSearchModel, params, user, groupId, scope).get("dossierNoCount");
    }


    private int getUsageCount(FileStoreGovSearchModel fileStoreGovSearchModel, User user, long groupId) {
        List<Object> params = new ArrayList<>();
        String scope = getScope(fileStoreGovSearchModel, user, groupId);
        return buildCountQueryForUsage(fileStoreGovSearchModel, params, user, groupId, scope).get("usageCount");
    }

    public Response search(HttpServletRequest request, HttpHeaders header, Company company, Locale locale, User user,
                           ServiceContext serviceContext, FileStoreGovSearchModel fileStoreGovSearchModel
    ) {
        BackendAuth auth = new BackendAuthImpl();
        //check authen
        if (!auth.isAuth(serviceContext)) {
            return Response.status(HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION).entity(new QLCDResponse(QLCDConstants.CODE_FAIL, "You are not authorized to perform this action")).build();
        }
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        LinkedHashMap<String, Object> paramsSQL = new LinkedHashMap<>();
        try {
            params = buildSearchParams(fileStoreGovSearchModel, user, serviceContext, header, company);
            paramsSQL = buildSearchParams(fileStoreGovSearchModel, user, serviceContext, header, company, false);
        } catch (NotFoundException e) {
            JSONObject searchResult = JSONFactoryUtil.createJSONObject();
            searchResult.put(ConstantUtils.DATA, new ArrayList<>());
            searchResult.put(ConstantUtils.TOTAL, 0);
            searchResult.put("usageCount", 0);
            searchResult.put("dossierNoCount", 0);
            return Response.status(HttpURLConnection.HTTP_OK).entity(searchResult.toJSONString()).build();
        } catch (UnauthorizationException e) {
            return Response.status(HttpStatus.SC_FORBIDDEN).entity(new QLCDResponse(QLCDConstants.CODE_FAIL, "Permission denied")).build();
        }
        long groupId = GetterUtil.getLong(header.getHeaderString(Field.GROUP_ID));
        Hits hits;
        SearchContext searchContext = new SearchContext();
        searchContext.setCompanyId(company.getCompanyId());
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        int total = 0;
        int dossierNoCount = 0;
        int usageCount = getUsageCount(fileStoreGovSearchModel, user, groupId);
//        int dossierNoCount = getDossierNoCount(fileStoreGovSearchModel, user, groupId);
        Integer start = fileStoreGovSearchModel.getStart();
        Integer end = fileStoreGovSearchModel.getEnd();
        String sort = fileStoreGovSearchModel.getSort();
        if (Validator.isNull(start)) {
            start = QueryUtil.ALL_POS;
        }
        if (Validator.isNull(end)) {
            end = QueryUtil.ALL_POS;
        }
        Sort[] sorts;
        //arrange follow create day
        if ("1".equals(sort)) {
            String dateSort = String.format(MessageUtil.getMessage(ConstantUtils.QUERY_NUMBER_SORT), Field.MODIFIED_DATE);
            sorts = new Sort[]{SortFactoryUtil.create(dateSort, Sort.LONG_TYPE,
                    GetterUtil.getBoolean(true))
            };
            //arrange follow file name
        } else if ("dossierNo".equals(sort)) {
            String dossierNoSort = String.format(MessageUtil.getMessage(ConstantUtils.QUERY_STRING_SORT), FileStoreGovTerm.DOSSIER_NO);
            sorts = new Sort[]{SortFactoryUtil.create(dossierNoSort, Sort.STRING_TYPE,
                    GetterUtil.getBoolean(false))
            };
        } else {
            String dateSort = String.format(MessageUtil.getMessage(ConstantUtils.QUERY_NUMBER_SORT), Field.CREATE_DATE);
            sorts = new Sort[]{SortFactoryUtil.create(dateSort, Sort.LONG_TYPE,
                    GetterUtil.getBoolean(true))
            };
        }
        JSONObject searchResult = JSONFactoryUtil.createJSONObject();
        JSONArray resultArr = JSONFactoryUtil.createJSONArray();
        JSONArray resultArrTest = JSONFactoryUtil.createJSONArray();
        try {
            List<Object[]> fileStoreGovList = FileStoreGovLocalServiceUtil.searchDynamic(paramsSQL, start, end);
            List<ServiceInfo> serviceList = ServiceInfoLocalServiceUtil.findByGroup(groupId);
            DictCollection govAgencyCollection = DictCollectionLocalServiceUtil
                    .fetchByF_dictCollectionCode(ConstantUtils.GOVERNMENT_AGENCY, groupId);

            for (Object[] row : fileStoreGovList) {
                JSONObject result = JSONFactoryUtil.createJSONObject();

                String dossierNo = row[0] != null ? row[0].toString() : "";
                String govAgencyCode= row[1] != null ? row[1].toString() : "";
                String serviceCode = row[2] != null ? row[2].toString() : "";
//                String typeNo = row[3] != null ? row[3].toString() : "";

                Optional<ServiceInfo> serviceOpt = serviceList.stream()
                        .filter(s -> Objects.equals(s.getServiceCode(), serviceCode))
                        .findFirst();

                DictItem govItem = DictItemLocalServiceUtil.fetchByF_dictItemCode(govAgencyCode, govAgencyCollection.getDictCollectionId(), groupId);

                if (serviceOpt.isPresent()) {
                    result.put(FileStoreGovTerm.SERVICE_NAME, serviceOpt.get().getServiceName());
                    result.put(FileStoreGovTerm.DOMAIN_NAME, serviceOpt.get().getDomainName());
                }

                if (govItem != null) {
                    result.put(FileStoreGovTerm.GOVAGENCYNAME, govItem.getItemName());
                }

                result.put(FileStoreGovTerm.DOSSIER_NO, dossierNo);
                result.put(FileStoreGovTerm.GOVAGENCYCODE, govAgencyCode);
                result.put(FileStoreGovTerm.SERVICECODE, serviceCode);
//                result.put(FileStoreGovTerm.TYPE_NO, document.get(FileStoreGovTerm.TYPE_NO));
                resultArrTest.put(result);
            }
            total = FileStoreGovLocalServiceUtil.countDynamic(paramsSQL, "");
            dossierNoCount = FileStoreGovLocalServiceUtil.countDynamic(paramsSQL, "DOSSIER_COUNT");

//            hits = FileStoreGovLocalServiceUtil.search(params, sorts, start, end, searchContext);
//            total = hits.getLength();

//            if (Validator.isNotNull(hits)) {
//                List<Document> resultList = hits.toList();
//                for (int i = 0; i < end - start; i++) {
//                    JSONObject result = JSONFactoryUtil.createJSONObject();
//                    Document document = resultList.get(i);
//                    result.put("fileStoreId", document.get(Field.ENTRY_CLASS_PK));
//                    result.put(FileStoreGovTerm.GROUPID, document.get(FileStoreGovTerm.GROUPID));
//                    result.put(FileStoreGovTerm.EMPLOYEEID, document.get(FileStoreGovTerm.EMPLOYEEID));
//                    result.put(FileStoreGovTerm.GOVAGENCYCODE, document.get(FileStoreGovTerm.GOVAGENCYCODE));
//                    if (Validator.isNotNull(document.get(FileStoreGovTerm.CREATEDATE))) {
//                        Date createDate = df.parse(document.get(FileStoreGovTerm.CREATEDATE));
//                        result.put(FileStoreGovTerm.CREATEDATE, dateFormat.format(createDate));
//
//                    }
//                    if (Validator.isNotNull(document.get(FileStoreGovTerm.MODIFIEDDATE))) {
//                        Date modifiedDate = df.parse(document.get(FileStoreGovTerm.MODIFIEDDATE));
//                        result.put(FileStoreGovTerm.MODIFIEDDATE, dateFormat.format(modifiedDate));
//                    }
//                    result.put(FileStoreGovTerm.FILEENTRYID, document.get(FileStoreGovTerm.FILEENTRYID));
//                    result.put(FileStoreGovTerm.FILEGOVCODE, document.get(FileStoreGovTerm.FILEGOVCODE));
//                    result.put(FileStoreGovTerm.SERVICECODE, document.get(FileStoreGovTerm.SERVICECODE));
//                    result.put(FileStoreGovTerm.OWNERTYPE, document.get(FileStoreGovTerm.OWNERTYPE));
//                    if (Validator.isNotNull(document.get(FileStoreGovTerm.OWNERDATE))) {
//                        result.put(FileStoreGovTerm.OWNERDATE, document.get(FileStoreGovTerm.OWNERDATE));
//                    }
//                    result.put(FileStoreGovTerm.OWNERNO, document.get(FileStoreGovTerm.OWNERNO));
//                    result.put(FileStoreGovTerm.OWNERNAME, document.get(FileStoreGovTerm.OWNERNAME));
//                    result.put(FileStoreGovTerm.PARTNO, document.get(FileStoreGovTerm.PARTNO));
//                    result.put(FileStoreGovTerm.FILENAME, document.get(FileStoreGovTerm.FILENAME));
//                    result.put(FileStoreGovTerm.DISPLAYNAME, document.get(FileStoreGovTerm.DISPLAYNAME));
//                    result.put(FileStoreGovTerm.CODENUMBER, document.get(FileStoreGovTerm.CODENUMBER));
//                    result.put(FileStoreGovTerm.CODENOTATION, document.get(FileStoreGovTerm.CODENOTATION));
//                    result.put(FileStoreGovTerm.DEPARTMENTISSUE, document.get(FileStoreGovTerm.DEPARTMENTISSUE));
//                    if (Validator.isNotNull(document.get(FileStoreGovTerm.ISSUEDATE))) {
//                        Date issueDate = df.parse(document.get(FileStoreGovTerm.ISSUEDATE));
//                        result.put(FileStoreGovTerm.ISSUEDATE, dateFormat.format(issueDate));
//                    }
//                    result.put(FileStoreGovTerm.ABSTRACTSS, document.get(FileStoreGovTerm.ABSTRACTSS));
//                    if (Validator.isNotNull(document.get(FileStoreGovTerm.VALIDTO))) {
//                        Date validTo = df.parse(document.get(FileStoreGovTerm.VALIDTO));
//                        result.put(FileStoreGovTerm.VALIDTO, dateFormat.format(validTo));
//                    }
//                    result.put(FileStoreGovTerm.VALIDSCOPE, document.get(FileStoreGovTerm.VALIDSCOPE));
//                    result.put(FileStoreGovTerm.FULLINFO, document.get(FileStoreGovTerm.FULLINFO));
//                    result.put(FileStoreGovTerm.SIZE_, document.get(FileStoreGovTerm.SIZE_));
//                    result.put(FileStoreGovTerm.PARTTYPE, document.get(FileStoreGovTerm.PARTTYPE));
//                    result.put(FileStoreGovTerm.PARTTYPEDETAIL, document.get(FileStoreGovTerm.PARTTYPEDETAIL));
//                    result.put(FileStoreGovTerm.DOSSIER_NO, document.get(FileStoreGovTerm.DOSSIER_NO));
//                    result.put(FileStoreGovTerm.DOSSIER_NAME, document.get(FileStoreGovTerm.DOSSIER_NAME));
//                    result.put(FileStoreGovTerm.ABSTRACTSS, document.get(FileStoreGovTerm.ABSTRACTSS));
//                    result.put(FileStoreGovTerm.GOVAGENCYNAME, document.get(FileStoreGovTerm.GOVAGENCYNAME));
//                    resultArr.put(result);
//                }
//            }

        } catch (Exception e) {
            _log.error(e);
        }

        searchResult.put(ConstantUtils.DATA, resultArrTest);
        searchResult.put(ConstantUtils.TOTAL, total);
        searchResult.put("usageCount", usageCount);
        searchResult.put("dossierNoCount", dossierNoCount);
        return Response.status(HttpURLConnection.HTTP_OK).entity(searchResult.toJSONString()).build();

    }

    private LinkedHashMap<String, Object> buildSearchParams(FileStoreGovSearchModel fileStoreGovSearchModel, User user, ServiceContext serviceContext, HttpHeaders header, Company company) throws NotFoundException, UnauthorizationException {
        return buildSearchParams(fileStoreGovSearchModel, user, serviceContext, header, company, true);
    }
    private LinkedHashMap<String, Object> buildSearchParams(FileStoreGovSearchModel fileStoreGovSearchModel, User user, ServiceContext serviceContext, HttpHeaders header, Company company, boolean isESParams) throws NotFoundException, UnauthorizationException {
        Integer start = fileStoreGovSearchModel.getStart();
        Integer end = fileStoreGovSearchModel.getEnd();
        String keyword = fileStoreGovSearchModel.getKeyword();
        Long folderId = fileStoreGovSearchModel.getFolderId();
        String isActive = fileStoreGovSearchModel.getIsActive();
        String govAgencyCode = fileStoreGovSearchModel.getGovAgencyCode();
        String govAgencyName = fileStoreGovSearchModel.getGovAgencyName();
        String partType = fileStoreGovSearchModel.getPartType();
        Boolean isSiblingSearch = fileStoreGovSearchModel.getSiblingSearch();
        String serviceCode = fileStoreGovSearchModel.getServiceCode();
        String departmentIssue = fileStoreGovSearchModel.getDepartmentIssue();
        String fromDate = convertNormalDateToLuceneDate(fileStoreGovSearchModel.getFromDate());
        String toDate = convertNormalDateToLuceneDate(fileStoreGovSearchModel.getToDate());
        String ownerNo = fileStoreGovSearchModel.getOwnerNo();
        String ownerName = fileStoreGovSearchModel.getOwnerName();
        String fileName = fileStoreGovSearchModel.getFileName();
        String displayName = fileStoreGovSearchModel.getDisplayName();
        String codeNumber = fileStoreGovSearchModel.getCodeNumber();
        String codeNotation = fileStoreGovSearchModel.getCodeNotation();
        String combinedCode = fileStoreGovSearchModel.getCombinedCode();
        String fullInfo = fileStoreGovSearchModel.getFullInfo();
        String dossierNo = fileStoreGovSearchModel.getDossierNo();
        String dossierName = fileStoreGovSearchModel.getDossierName();
        String abtractss = fileStoreGovSearchModel.getAbstractss();
        String issueDateFrom = convertNormalDateToLuceneDate(fileStoreGovSearchModel.getIssueDateFrom());
        String issueDateTo = convertNormalDateToLuceneDate(fileStoreGovSearchModel.getIssueDateTo());
        String domainCode = fileStoreGovSearchModel.getDomainCode();
        long groupId = GetterUtil.getLong(header.getHeaderString(Field.GROUP_ID));
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
        if (!Objects.isNull(domainCode) && !domainCode.isEmpty() && (Objects.isNull(serviceCode) || serviceCode.isEmpty())) {
            List<ServiceInfo> serviceInfoList = ServiceInfoLocalServiceUtil.getServiceInfosByGroupId(groupId, -1, -1);
            List<String> serviceCodeList = serviceInfoList.stream().filter(service -> Objects.equals(service.getDomainCode(), domainCode)).map(service -> service.getServiceCode()).collect(Collectors.toList());
            if (serviceCodeList.isEmpty()) throw new NotFoundException();
            String joinedCodes = String.join(",", serviceCodeList);
            params.put(FileStoreTerm.SERVICE_CODE, joinedCodes);
        } else if (!Objects.isNull(serviceCode)) {
            params.put(FileStoreTerm.SERVICE_CODE, serviceCode);
        }
        params.put(Field.USER_ID, user.getUserId());
        params.put(Field.COMPANY_ID, company.getCompanyId());
        params.put(Field.GROUP_ID, String.valueOf(groupId));
        Employee employee = EmployeeLocalServiceUtil.fetchByFB_MUID(user.getUserId());
        if (Validator.isNotNull(employee)) {
            if (Validator.isNotNull(employee.getScope())) {
                DictCollection dictCollection = DictCollectionLocalServiceUtil.fetchByF_dictCollectionCode(GOVERNMENT_AGENCY, groupId);
                if (Validator.isNotNull(dictCollection)) {
                    DictItem dictItem = DictItemLocalServiceUtil.fetchByF_dictItemCode(employee.getScope(), dictCollection.getDictCollectionId(), groupId);
                    _log.info("search:: dictItem = " + dictItem);
                    String scopeList = null;
                    if (Validator.isNotNull(dictItem)) {
                        scopeList = ScopeUtils.getAllSubUnit(dictItem, dictCollection.getDictCollectionId(), groupId);
                        _log.info("search:: scopeList = " + scopeList);
                    }
                    if (scopeList != null) {
                        if (isESParams) params.put(FileStoreGovTerm.GOVAGENCYCODE, SpecialCharacterUtils.splitSpecialNoComma(scopeList));
                        else params.put(FileStoreGovTerm.GOVAGENCYCODE, scopeList);
                    } else {
                        if (isESParams) params.put(FileStoreGovTerm.GOVAGENCYCODE, SpecialCharacterUtils.splitSpecialNoComma(employee.getScope()));
                        else params.put(FileStoreGovTerm.GOVAGENCYCODE, employee.getScope());
                    }
                }
            }
        }

        if (Validator.isNotNull(govAgencyName) && !Validator.isNotNull(govAgencyCode)) {
            govAgencyCode = getItemCodeByName(govAgencyName.trim());
        }

        if (Validator.isNotNull(govAgencyCode)) {
            if (Validator.isNull(isSiblingSearch) || !isSiblingSearch) {
                if (isESParams) govAgencyCode = SpecialCharacterUtils.splitSpecialNoComma(govAgencyCode.trim());
                else govAgencyCode = govAgencyCode.trim();
                params.put(FileStoreGovTerm.GOVAGENCYCODE, govAgencyCode);
            } else {
                String[] agencyList = govAgencyCode.split(",");
                StringBuilder scopeList = new StringBuilder();

                for (int i = 0; i < agencyList.length; i++) {
                    DictCollection dictCollection = DictCollectionLocalServiceUtil.fetchByF_dictCollectionCode(GOVERNMENT_AGENCY, groupId);
                    if (Validator.isNotNull(dictCollection)) {
                        DictItem dictItem = DictItemLocalServiceUtil.fetchByF_dictItemCode(agencyList[i], dictCollection.getDictCollectionId(), groupId);
                        String scope = null;
                        if (Validator.isNotNull(dictItem)) {
                            scope = ScopeUtils.getAllSubUnit(dictItem, dictCollection.getDictCollectionId(), groupId);
                        }
                        if (scope != null) {
                            if (isESParams) scopeList.append(SpecialCharacterUtils.splitSpecialNoComma(scope));
                            else scopeList.append(scope);
                        }
                    }
                }
                params.put(FileStoreGovTerm.GOVAGENCYCODE, scopeList.toString());
            }
        }

        if (Validator.isNotNull(keyword)) {
            params.put(Field.KEYWORD_SEARCH, keyword.trim());
        }
        if (Validator.isNotNull(folderId)) {
            Response fileStoreFolderValidator = FileStoreFolderValidator.validate(folderId, user.getUserId(), null, serviceContext);
            if (fileStoreFolderValidator.getStatus() != HttpStatus.SC_OK) {
                throw new UnauthorizationException();
            }
            if (isESParams) params.put(Field.FOLDER_ID, StringPool.COMMA + folderId + StringPool.COMMA);
            else params.put(Field.FOLDER_ID, folderId);
        }

//        if(Validator.isNotNull(govAgencyCode)){
//            params.put(FileStoreGovTerm.GOVAGENCYCODE, govAgencyCode);
//        }

        if (Validator.isNotNull(isActive)) {
            params.put(FileStoreTerm.IS_ACTIVE, "0");
        } else {
            params.put(FileStoreTerm.IS_ACTIVE, "1");
        }

        if (Validator.isNotNull(departmentIssue)) {
            params.put(FileStoreTerm.DEPARTMENT_ISSUE, departmentIssue.trim());
        }

        if (Validator.isNotNull(fromDate)) {
            params.put(FileStoreTerm.FROM_DATE, fromDate.trim());
        } else {
            params.put(FileStoreTerm.FROM_DATE, convertNormalDateToLuceneDate("01/01/1800"));
        }


        if (Validator.isNotNull(toDate)) {
            params.put(FileStoreTerm.TO_DATE, toDate.trim());
        } else {
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDate = today.format(formatter);
            params.put(FileStoreTerm.TO_DATE, convertNormalDateToLuceneDate(formattedDate));
        }
        // 10/6/2024 HieuLA bảo không tìm theo partType nữa
//        if(Validator.isNotNull(partType)){
//            params.put(FileStoreGovTerm.PARTTYPE, partType);
//        }
//        else {
//            params.put(FileStoreGovTerm.PARTTYPE, DossierPartType.ADMINISTRATIVE_FORMALITIES_RESULT.getValue());
//        }

        if (Validator.isNotNull(ownerNo)) {
            params.put(FileStoreGovTerm.OWNERNO, ownerNo.trim());
        }
        if (Validator.isNotNull(ownerName)) {
            params.put(FileStoreGovTerm.OWNERNAME, ownerName.trim());
        }
        if (Validator.isNotNull(fileName)) {
            params.put(FileStoreGovTerm.FILENAME, fileName.trim());
        }
        if (Validator.isNotNull(displayName)) {
            params.put(FileStoreGovTerm.DISPLAYNAME, displayName.trim());
        }
        if (Validator.isNotNull(codeNumber)) {
            params.put(FileStoreGovTerm.CODENUMBER, codeNumber.trim());
        }
        if (Validator.isNotNull(codeNotation)) {
            params.put(FileStoreGovTerm.CODENOTATION, codeNotation.trim());
        }

        if (Validator.isNotNull(combinedCode)) {
            combinedCode = SpecialCharacterUtils.splitSpecialNoComma(combinedCode.trim());
            params.put(FileStoreGovTerm.COMBINED_CODE, combinedCode);
        }

        if (Validator.isNotNull(fullInfo)) {
            params.put(FileStoreGovTerm.FULLINFO, fullInfo.trim());
        }
        if (Validator.isNotNull(dossierNo)) {
            // Không tìm được dữ liệu với ký tự '-' -> thay bằng space
            String dossierNoStr = dossierNo.trim().replace(StringPool.DASH, StringPool.SPACE);
            params.put(FileStoreGovTerm.DOSSIER_NO, dossierNoStr);
        }
        if (Validator.isNotNull(dossierName)) {
            params.put(FileStoreGovTerm.DOSSIER_NAME, dossierName.trim());
        }
        if (Validator.isNotNull(abtractss)) {
            params.put(FileStoreGovTerm.ABSTRACTSS, abtractss.trim());
        }
        if (Validator.isNotNull(issueDateFrom)) {
            params.put(FileStoreGovTerm.ISSUE_DATE_FROM, issueDateFrom.trim());
        }
        if (Validator.isNotNull(issueDateTo)) {
            params.put(FileStoreGovTerm.ISSUE_DATE_TO, issueDateTo.trim());
        }

        return params;
    }


    private void getUsageCountAndDossierNoCount(Hits hits, AtomicInteger usageCount, AtomicInteger dossierNoCount) {
        if (hits == null) return;
        Set<String> dossierNoSet = hits.toList()
                .parallelStream()
                .peek(document -> usageCount.addAndGet(getUsageCount(document)))
                .map(document -> document.get(FileStoreGovTerm.DOSSIER_NO_SEARCH))
                .filter(Objects::nonNull)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        dossierNoCount.addAndGet(dossierNoSet.size());
    }

    private int getUsageCount(Document document) {
        List<FileGovUsedHistory> list = FileGovUsedHistoryLocalServiceUtil.getBysearchByFSGA(Long.parseLong(document.get("entryClassPK")), "Tái sử dụng");
//                List<FileGovUsedHistory> authenUsedFiles = list.stream().filter(file -> file.getUserId() == user.getUserId()).collect(Collectors.toList());
        return list.size();
    }

    private int getUsageCount(long filestoregovId) {
        List<FileGovUsedHistory> list = FileGovUsedHistoryLocalServiceUtil.getBysearchByFSGA(filestoregovId, "Tái sử dụng");
        return list.size();
    }


    public static String getItemCodeByName(String govAgencyName) {
        String sqlItem = "select ITEMCODE \n" +
                "from OPENCPS_DICTITEM \n" +
                "where ITEMNAME = ?";
        List<Object> params = new ArrayList<>();
        String govAgencyCode = null;
        params.add(govAgencyName);
        JSONArray dataArray = executeDynamicQuery(sqlItem, params);
        for (int i = 0; i < Objects.requireNonNull(dataArray).length(); i++) {
            JSONObject obj = dataArray.getJSONObject(i);
            govAgencyCode = obj.getString("ITEMCODE");
            if (govAgencyCode != null && !govAgencyCode.isEmpty()) {
                return govAgencyCode;
            }
        }
        return govAgencyCode;
    }

    public static String checkOtherDictItem(String departmentIssue) {
        departmentIssue = departmentIssue.trim();
        String sqlItem = "select ITEMNAME \n" +
                "from OTHER_DICTITEM \n" +
                "where ITEMNAME = ?";
        List<Object> params = new ArrayList<>();
        String itemName = "";
        params.add(departmentIssue);
        JSONArray dataArray = executeDynamicQuery(sqlItem, params);
        for (int i = 0; i < Objects.requireNonNull(dataArray).length(); i++) {
            JSONObject obj = dataArray.getJSONObject(i);
            itemName = obj.getString("ITEMNAME");
            if (itemName != null && !itemName.isEmpty()) {
                return itemName;
            }
        }
        return itemName;
    }

    public static JSONArray executeDynamicQuery(String sql, List<Object> params) {
        DataSource dataSource = InfrastructureUtil.getDataSource();

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement pst = connection.prepareStatement(sql)
        ) {
            // Gán tham số nếu có
            if (params != null) {
                for (int i = 0; i < params.size(); i++) {
                    pst.setObject(i + 1, params.get(i)); // chỉ số bắt đầu từ 1
                }
            }

            try (ResultSet rs = pst.executeQuery()) {
                JSONArray results = JSONFactoryUtil.createJSONArray();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    JSONObject row = JSONFactoryUtil.createJSONObject();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnLabel(i);
                        Object value = rs.getObject(i);
                        row.put(columnName, value);
                    }
                    results.put(row);
                }

                return results;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Response getDossierPart(HttpServletRequest request, HttpHeaders header, Company company, Locale locale, User user,
                                   ServiceContext serviceContext) {
        BackendAuth auth = new BackendAuthImpl();
        //check authen
        if (!auth.isAuth(serviceContext)) {
            return Response.status(HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION).entity(new QLCDResponse(QLCDConstants.CODE_FAIL, "You are not authorized to perform this action")).build();
        }
        JSONObject searchResult = JSONFactoryUtil.createJSONObject();
        JSONArray resultArr = JSONFactoryUtil.createJSONArray();
        List<String> partNameList = new ArrayList<>();
        //get all
        List<DossierPart> dossierPartArr = DossierPartLocalServiceUtil.getDossierParts(-1, -1);
//        dossierPartArr = dossierPartArr.stream().filter(distinctByKey(DossierPartModel::getPartNo))
//                .filter(distinctByKey(DossierPartModel::getPartName))
//                .sorted(Comparator.comparing(DossierPartModel::getPartName))
//                .collect(Collectors.toList());
        for (DossierPart dossierPart : dossierPartArr) {
            JSONObject result = JSONFactoryUtil.createJSONObject();
            result.put("partName", dossierPart.getPartName());
            result.put("partNo", dossierPart.getPartNo());
            result.put("partType", dossierPart.getPartType());
            result.put("partTypeDetail", dossierPart.getPartTypeDetail());
            resultArr.put(result);
        }
        searchResult.put(ConstantUtils.DATA, resultArr);
        searchResult.put(ConstantUtils.TOTAL, resultArr.length());
        return Response.status(HttpURLConnection.HTTP_OK).entity(searchResult.toString()).build();
    }

    @Override
    public Response getDossierPartNew(HttpServletRequest request, HttpHeaders header, Company company, Locale locale, User user, ServiceContext serviceContext) {
        BackendAuth auth = new BackendAuthImpl();
        if (!auth.isAuth(serviceContext)) {
            return Response.status(HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION).entity(new QLCDResponse(QLCDConstants.CODE_FAIL, "You are not authorized to perform this action")).build();
        }
        long groupId = GetterUtil.getLong(header.getHeaderString(Field.GROUP_ID));
        JSONObject searchResult = JSONFactoryUtil.createJSONObject();
        JSONArray resultArr = JSONFactoryUtil.createJSONArray();
        List<String> partNameList = new ArrayList<>();
        //get all
        List<DossierPart> dossierParts = DossierPartLocalServiceUtil.findByG(groupId);
        for (DossierPart dossierPart : dossierParts) {
            JSONObject result = JSONFactoryUtil.createJSONObject();
            result.put("partName", dossierPart.getPartName());
            result.put("partNo", dossierPart.getPartNo());
            result.put("partType", dossierPart.getPartType());
            result.put("partTypeDetail", dossierPart.getPartTypeDetail());
            resultArr.put(result);
        }
        searchResult.put(ConstantUtils.DATA, resultArr);
        searchResult.put(ConstantUtils.TOTAL, resultArr.length());
        return Response.status(HttpURLConnection.HTTP_OK).entity(searchResult.toString()).build();
    }

    public <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(splitSpecialForDossierPart(t)), Boolean.TRUE) == null;
    }

    private <T> T splitSpecialForDossierPart(T value) {
        if (value instanceof DossierPart) {
            DossierPart dossierPart = (DossierPart) value;
            dossierPart.setPartNo(dossierPart.getPartNo().trim());
            dossierPart.setPartName(SpecialCharacterUtils.removeSpecialCharacter(dossierPart.getPartName()));
            return (T) dossierPart;
        }
        return null;
    }

    @Override
    public Response getListService(HttpServletRequest request, HttpHeaders header, Company company, Locale locale, User user, ServiceContext serviceContext) {
        BackendAuth auth = new BackendAuthImpl();
        if (!auth.isAuth(serviceContext)) {
            return Response.status(HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION).entity(MessageUtil.getMessage(ConstantUtils.API_JSON_MESSAGE_NONAUTHORATIVE))
                    .build();
        }

        JSONObject response = JSONFactoryUtil.createJSONObject();
        int total = 0;
        JSONArray data = JSONFactoryUtil.createJSONArray();
        long groupId = GetterUtil.getLong(header.getHeaderString(Field.GROUP_ID));
        List<ServiceInfo> serviceInfoList = ServiceInfoLocalServiceUtil.getServiceInfosByGroupId(groupId, -1, -1);
        for (ServiceInfo serviceCode : serviceInfoList) {
            JSONObject oneService = JSONFactoryUtil.createJSONObject();
            if (Validator.isNotNull(serviceCode)) {
                total++;
                oneService.put("serviceCode", serviceCode.getServiceCode());
                oneService.put("serviceName", serviceCode.getServiceName());
                oneService.put("domainCode", serviceCode.getDomainCode());
                data.put(oneService);
            }
        }
        response.put("total", total);
        response.put("data", data);

        return Response.status(HttpStatus.SC_OK).entity(response.toJSONString()).build();
    }



    @Override
    @Transactional
    public Response addNewFileStoreGov(HttpServletRequest request, HttpHeaders header, Company company, Locale locale, User user, ServiceContext serviceContext,
                                       Attachment file, long newFileEntryId, String serviceCode, String ownerType, String ownerNo, String ownerName, String ownerDate, String partNo, String fileName, String codeNumber,
                                       String codeNotation, String departmentIssue, String abstractSS, String partType, String partTypeDetail, String validTo, String validScope, String issueDate, String fullInfo,
                                       String dossierNo, String dossierName) throws PortalException {
        BackendAuth auth = new BackendAuthImpl();
        //check authen
        if (!auth.isAuth(serviceContext)) {
            return Response.status(HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION).entity(new QLCDResponse(QLCDConstants.CODE_FAIL, "You are not authorized to perform this action")).build();
        }
//        JSONObject validateDossierNo = DossierManagementImpl.isValidDossierNo(dossierNo);
//        boolean validDossierNo = validateDossierNo.getBoolean("valid");
//        if (!validDossierNo) {
//            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(new QLCDResponse(QLCDConstants.CODE_FAIL, (String) validateDossierNo.get("message"))).build();
//        }
        long groupId = GetterUtil.getLong(header.getHeaderString(Field.GROUP_ID));
        Employee employee = EmployeeLocalServiceUtil.fetchByFB_MUID(user.getUserId());
        Date now = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String strDate = dateFormat.format(now);
        long id = CounterLocalServiceUtil.increment(FileStoreGov.class.getName());
        FileStoreGov fileStoreGov = FileStoreGovLocalServiceUtil.createFileStoreGov(id);
        partType = String.valueOf(DossierPartType.ADMINISTRATIVE_FORMALITIES_RESULT.getValue());
        if (Validator.isNotNull(newFileEntryId)) {
            FileEntry fileEntry = DLAppLocalServiceUtil.getFileEntry(newFileEntryId);
            fileStoreGov.setGroupId(groupId);
            if (Validator.isNotNull(employee)) {
                fileStoreGov.setEmployeeId(employee.getEmployeeId());
            }
            fileStoreGov.setGovAgencyCode(employee.getScope());
            fileStoreGov.setGroupId(groupId);
            fileStoreGov.setCreateDate(APIDateTimeUtils._stringToDate(strDate, "dd/MM/yyyy HH:mm:ss"));
            fileStoreGov.setModifiedDate(APIDateTimeUtils._stringToDate(strDate, "dd/MM/yyyy HH:mm:ss"));

            fileStoreGov.setFileEntryId(newFileEntryId);
            if (Validator.isNotNull(ownerDate)) {
                fileStoreGov.setOwnerDate(ownerDate);
            }
            if (Validator.isNotNull(ownerName)) {
                fileStoreGov.setOwnerName(ownerName);
            }
            if (Validator.isNotNull(ownerNo)) {
                fileStoreGov.setOwnerNo(ownerNo);
            }
            if (Validator.isNotNull(ownerType)) {
                fileStoreGov.setOwnerType(ownerType);
            }
            fileStoreGov.setPartNo(partNo);
            fileStoreGov.setFileName(fileName);
            fileStoreGov.setDisplayName(fileEntry.getFileName());
            fileStoreGov.setServiceCode(serviceCode);
            fileStoreGov.setCodeNumber(codeNumber);
            fileStoreGov.setCodeNotation(codeNotation);
            fileStoreGov.setDepartmentIssue(departmentIssue);
            fileStoreGov.setAbstractSS(abstractSS);
            fileStoreGov.setValidTo(APIDateTimeUtils._stringToDate(validTo, "dd/MM/yyyy"));
            fileStoreGov.setValidScope(validScope);
            fileStoreGov.setFullInfo(fullInfo);
            fileStoreGov.setIssueDate(APIDateTimeUtils._stringToDate(issueDate, "dd/MM/yyyy"));
            fileStoreGov.setSize_(fileEntry.getSize());
            fileStoreGov.setPartType(Long.parseLong((partType)));
            fileStoreGov.setPartTypeDetail(Long.parseLong(partTypeDetail));
            fileStoreGov.setShared(1);
            fileStoreGov.setIsActive(1);
            fileStoreGov.setDossierNo(dossierNo);
            fileStoreGov.setDossierName(dossierName);
            String fileGovCode = generateFileGovCode(ownerNo, ownerName, ownerDate, serviceCode, partNo, Integer.parseInt((partType)), codeNumber, codeNotation, dossierNo);
            _log.info("addNewFileStoreGov:: fileGovCode = " + fileGovCode);
            fileStoreGov.setFileGovCode(fileGovCode);
            String typeNo = generateTypeNo(Integer.parseInt((partType)), partNo, serviceCode);
            fileStoreGov.setTypeNo(typeNo);
            FileStoreGovLocalServiceUtil.updateFileStoreGov(fileStoreGov);
            FileGovUsedHistory fileGovUsedHistoryAfterUpdating = addToFileStoreGovHistory(id, groupId, user, strDate);
            _log.info("addNewFileStoreGov:: fileGovUsedHistory after updating value = " + fileGovUsedHistoryAfterUpdating);
        }
        return Response.status(HttpURLConnection.HTTP_OK).entity(JSONFactoryUtil.looseSerialize(fileStoreGov)).build();
    }

    private FileGovUsedHistory addToFileStoreGovHistory(long fileStoreGovId, long groupId, User user, String strDate) {
        long fileGovUsedHistoryId = CounterLocalServiceUtil.increment(FileGovUsedHistory.class.getName());
        _log.info("addNewFileStoreGov:: fileGovUsedHistoryId = " + fileGovUsedHistoryId);
        FileGovUsedHistory fileGovUsedHistory = FileGovUsedHistoryLocalServiceUtil.createFileGovUsedHistory(fileGovUsedHistoryId);
        _log.info("addNewFileStoreGov:: fileGovUsedHistory before updating value = " + fileGovUsedHistory);
        fileGovUsedHistory.setFileStoreGovId(fileStoreGovId);
        fileGovUsedHistory.setGroupId(groupId);
        fileGovUsedHistory.setActionSS("Giấy tờ được thêm mới vào kho dữ liệu");
        fileGovUsedHistory.setUserId(user.getUserId());
        fileGovUsedHistory.setCreateDate(APIDateTimeUtils._stringToDate(strDate, "dd/MM/yyyy HH:mm:ss"));
        return FileGovUsedHistoryLocalServiceUtil.updateFileGovUsedHistory(fileGovUsedHistory);
    }

    // [READ] (get Detail fileStoreGov)
    @Override
    public Response readFileStoreGov(HttpServletRequest request, HttpHeaders header, Company company,
                                     Locale locale, User user, ServiceContext serviceContext,
                                     long id) throws PortalException {
        BackendAuth auth = new BackendAuthImpl();
        //check authen
        if (!auth.isAuth(serviceContext)) {
            return Response.status(HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION).entity(new QLCDResponse(QLCDConstants.CODE_FAIL, "You are not authorized to perform this action")).build();
        }
        long groupId = GetterUtil.getLong(header.getHeaderString(Field.GROUP_ID));
        FileStoreGov fileStoreGov = FileStoreGovLocalServiceUtil.fetchFileStoreGov(id);
        Response fileStoreGovValidator = FileStoreGovValidator.validate(id, user.getUserId(), fileStoreGov, serviceContext);
        if (fileStoreGovValidator.getStatus() != HttpStatus.SC_OK) {
            return Response.status(fileStoreGovValidator.getStatus())
                    .entity(fileStoreGovValidator.getEntity()).build();
        }
        if (Validator.isNotNull(fileStoreGov)) {
            List<FileFolder> fileFolders = new ArrayList<>();
            boolean editable = true;
            List<DossierFile> dossierFiles = DossierFileLocalServiceUtil.findByFileStoreId(fileStoreGov.getFileStoreGovId(), groupId);
            if (Validator.isNotNull(dossierFiles) && dossierFiles.size() > 0) {
                editable = false;
            }
            StringBuilder folderList = new StringBuilder();
            for (FileFolder fileFolder : fileFolders) {
                folderList.append(fileFolder.getFileStoreFolderId()).append(StringPool.COMMA);
            }

            JSONObject result = ObjectConverterUtil.objectToJSON(FileStoreGovModel.class, fileStoreGov).put("folderList", folderList.toString()).put("editable", editable);

            ServiceInfo serviceInfo = ServiceInfoLocalServiceUtil.getByCode(groupId, fileStoreGov.getServiceCode());
            if (Validator.isNotNull(serviceInfo)) {
                result.put("serviceName", serviceInfo.getServiceName());
            }
            String otherDepartmentIssue = checkOtherDictItem(fileStoreGov.getDepartmentIssue());
            result.put("otherDepartmentIssue", otherDepartmentIssue);
            result.put("isOtherDepartmentIssue", otherDepartmentIssue != null && !otherDepartmentIssue.isEmpty());

            String otherFileName = checkOtherDictItem(fileStoreGov.getFileName());
            result.put("otherFileName", otherFileName);
            result.put("isOtherFileName", otherFileName != null && !otherFileName.isEmpty());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result.toJSONString()).build();
        } else {
            ErrorMsg error = new ErrorMsg();
            error.setMessage("Not found fileStoreGov");
            error.setCode(HttpURLConnection.HTTP_BAD_REQUEST);
//            error.setDescription(MessageUtil.getMessage(ConstantUtils.API_MESSAGE_NOTFOUND));
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(error).build();
        }

    }

    @Override
    public Response getFileStoreGov(HttpServletRequest request, HttpHeaders header, Company company,
                                    Locale locale, User user, ServiceContext serviceContext,
                                    String dossierNo,
                                    String serviceCode,
                                    String govAgencyCode) {
//        BackendAuth auth = new BackendAuthImpl();
//        if (!auth.isAuth(serviceContext)) {
//            return Response.status(HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION).entity(new QLCDResponse(QLCDConstants.CODE_FAIL, "You are not authorized to perform this action")).build();
//        }
        try {
            DynamicQuery dq = FileStoreGovLocalServiceUtil.dynamicQuery();
            if (Validator.isNotNull(dossierNo)) {
                dq.add(RestrictionsFactoryUtil.eq("dossierNo", dossierNo));
            }
            if (Validator.isNotNull(serviceCode)) {
                dq.add(RestrictionsFactoryUtil.eq("serviceCode", serviceCode));
            }
            if (Validator.isNotNull(govAgencyCode)) {
                dq.add(RestrictionsFactoryUtil.eq("govAgencyCode", govAgencyCode));
            }

            List<FileStoreGov> fileStoreGovList = FileStoreGovLocalServiceUtil.dynamicQuery(dq);
            if (!fileStoreGovList.isEmpty()) {
                FileStoreGov firstElement = fileStoreGovList.get(0);
                JSONObject result = JSONFactoryUtil.createJSONObject();

                long groupId = GetterUtil.getLong(header.getHeaderString(Field.GROUP_ID));
                ServiceInfo serviceInfo = ServiceInfoLocalServiceUtil.getByCode(groupId, serviceCode);

                result.put(FileStoreGovTerm.SERVICECODE, firstElement.getServiceCode());
                result.put(FileStoreGovTerm.DOMAIN_CODE, serviceInfo.getDomainCode());
                result.put(FileStoreGovTerm.OWNERTYPE, Integer.valueOf(firstElement.getOwnerType()));
                result.put(FileStoreGovTerm.OWNERNAME, firstElement.getOwnerName());
                result.put(FileStoreGovTerm.OWNERDATE, firstElement.getOwnerDate());
                result.put(FileStoreGovTerm.OWNERNO, firstElement.getOwnerNo());
                result.put(FileStoreGovTerm.DOSSIER_NO, firstElement.getDossierNo());


                JSONArray files = JSONFactoryUtil.createJSONArray();
                for (FileStoreGov fileStoreGov : fileStoreGovList) {
                    JSONObject file = JSONFactoryUtil.createJSONObject();
                    file.put(FileStoreGovTerm.FILEENTRYID, fileStoreGov.getFileEntryId());
                    file.put(FileStoreGovTerm.FILENAME, fileStoreGov.getFileName());
                    file.put(FileStoreGovTerm.ISSUEDATE, fileStoreGov.getIssueDate());
                    file.put(FileStoreGovTerm.CODENUMBER, fileStoreGov.getCodeNumber());
                    file.put(FileStoreGovTerm.CODENOTATION, fileStoreGov.getCodeNotation());

                    file.put(FileStoreGovTerm.ABSTRACTSS, fileStoreGov.getAbstractSS());
                    file.put(FileStoreGovTerm.VALIDTO, fileStoreGov.getValidTo());
                    file.put(FileStoreGovTerm.VALIDSCOPE, fileStoreGov.getValidScope());
                    file.put(FileStoreGovTerm.PARTNO, fileStoreGov.getPartNo());
                    file.put(FileStoreGovTerm.PARTTYPE, Long.valueOf(fileStoreGov.getPartType()));
                    file.put(FileStoreGovTerm.FILESTOREGOVID, fileStoreGov.getFileStoreGovId());
                    file.put(FileStoreGovTerm.DISPLAYNAME, fileStoreGov.getDisplayName());
                    file.put(FileStoreGovTerm.GROUPID, fileStoreGov.getGroupId());
                    file.put(FileStoreGovTerm.PARTTYPEDETAIL, fileStoreGov.getPartTypeDetail());
                    file.put(FileStoreGovTerm.FILESTOREGOVID, fileStoreGov.getFileStoreGovId());
                    file.put("typeNo", fileStoreGov.getTypeNo());
                    file.put("fileGovCode", fileStoreGov.getFileGovCode());


                    // Khi thêm mới giấy tờ bắt buộc ký số nên mặc định lấy giấy tờ lên đã ký số
                    file.put(FileStoreGovTerm.IS_SIGNED, true);
                    dq.add(RestrictionsFactoryUtil.eq("fileStoreGovId", fileStoreGov.getFileStoreGovId()));
//                    long count = OtherDictItemLocalServiceUtil.dynamicQueryCount(dq);

                    long count = 0;
                    String otherDepartmentIssue = checkOtherDictItem(fileStoreGov.getDepartmentIssue());
                    if (!otherDepartmentIssue.isEmpty()) {
                        file.put(FileStoreGovTerm.OTHER_DEPARTMENT_ISSUE, fileStoreGov.getDepartmentIssue());
                        file.put(FileStoreGovTerm.DEPARTMENTISSUE, StringUtils.EMPTY);
                        file.put("isOtherDepartmentChecked", true);

                    } else {
                        file.put(FileStoreGovTerm.DEPARTMENTISSUE, fileStoreGov.getDepartmentIssue());
                        file.put(FileStoreGovTerm.OTHER_DEPARTMENT_ISSUE, StringUtils.EMPTY);
                        file.put("isOtherDepartmentChecked", false);
                    }

                    String otherFileName = checkOtherDictItem(fileStoreGov.getFileName());
                    if (!otherFileName.isEmpty()) {
                        file.put(FileStoreGovTerm.OTHER_FILE_NAME, fileStoreGov.getFileName());
                        file.put(FileStoreGovTerm.FILENAME, StringUtils.EMPTY);
                        file.put("isOtherFileNameChecked", true);

                    } else {
                        file.put(FileStoreGovTerm.FILENAME, fileStoreGov.getFileName());
                        file.put(FileStoreGovTerm.OTHER_FILE_NAME, StringUtils.EMPTY);
                        file.put("isOtherFileNameChecked", false);
                    }

                    files.put(file);
                }
                result.put(DossierActionTerm.FILES, files);
                return Response.ok(result.toJSONString()).build();
            } else {
                return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).build();
            }
        } catch (Exception e) {
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).build();
        }
    }


    @Override
    public Response updateActive(HttpServletRequest request, HttpHeaders header,
                                 Company company, Locale locale, User user, ServiceContext serviceContext, RecipientInputModel model, int isActive) throws InterruptedException {
        //authentication
        BackendAuth auth = new BackendAuthImpl();
        if (!auth.isAuth(serviceContext)) {
            return Response.status(HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION).entity(MessageUtil.getMessage(ConstantUtils.API_JSON_MESSAGE_NONAUTHORATIVE))
                    .build();
        }
        //them nhieu record chinh sua
        Date now = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String strDate = dateFormat.format(now);
        long[] fileStoreIdList = model.getFileStoreIDList();
        for (Long fileStoreId : fileStoreIdList) {
            FileStoreGov updateFileStoreGov = FileStoreGovLocalServiceUtil.fetchFileStoreGov(fileStoreId);
            Response fileStoreGovValidator = FileStoreGovValidator.validateActions(fileStoreId, user.getUserId(), updateFileStoreGov, serviceContext);
            if (fileStoreGovValidator.getStatus() != HttpStatus.SC_OK) {
                return Response.status(fileStoreGovValidator.getStatus())
                        .entity(fileStoreGovValidator.getEntity()).build();
            }
            if (Validator.isNotNull(updateFileStoreGov)) {
                try {
                    updateFileStoreGov.setModifiedDate(APIDateTimeUtils._stringToDate(strDate, "dd/MM/yyyy HH:mm:ss"));
                    updateFileStoreGov.setIsActive(isActive);
                    FileStoreGovLocalServiceUtil.updateFileStoreGov(updateFileStoreGov);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                return Response.status(HttpStatus.SC_OK).entity("FAILED").build();
            }
        }
        return Response.status(HttpStatus.SC_OK).entity("SUCCESS").build();
    }

    @Override
    public Response delete(HttpServletRequest request, HttpHeaders header,
                           Company company, Locale locale, User user, ServiceContext serviceContext,
                           String dossierNo, String serviceCode, String govAgencyCode) throws InterruptedException {
        //authentication
        BackendAuth auth = new BackendAuthImpl();
        if (!auth.isAuth(serviceContext)) {
            return Response.status(HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION).entity(MessageUtil.getMessage(ConstantUtils.API_JSON_MESSAGE_NONAUTHORATIVE))
                    .build();
        }
        DynamicQuery dq = FileStoreGovLocalServiceUtil.dynamicQuery();
        if (Validator.isNotNull(dossierNo)) {
            dq.add(RestrictionsFactoryUtil.eq("dossierNo", dossierNo));
        }
        if (Validator.isNotNull(serviceCode)) {
            dq.add(RestrictionsFactoryUtil.eq("serviceCode", serviceCode));
        }
        if (Validator.isNotNull(govAgencyCode)) {
            dq.add(RestrictionsFactoryUtil.eq("govAgencyCode", govAgencyCode));
        }

        List<FileStoreGov> fileStoreGovList = FileStoreGovLocalServiceUtil.dynamicQuery(dq);
        if (!fileStoreGovList.isEmpty()) {
            for (FileStoreGov fileStoreGov : fileStoreGovList) {
                Date now = new Date();
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String strDate = dateFormat.format(now);
                fileStoreGov.setModifiedDate(APIDateTimeUtils._stringToDate(strDate, "dd/MM/yyyy HH:mm:ss"));
                fileStoreGov.setIsActive(0);
                FileStoreGovLocalServiceUtil.updateFileStoreGov(fileStoreGov);
            }
        }
        return Response.status(HttpStatus.SC_OK).entity("SUCCESS").build();
    }

    // [DELETE]
    @Override
    public Response deleteFileStoreGov(HttpServletRequest request, HttpHeaders header, Company company, Locale locale, User user, ServiceContext serviceContext, String ids) {
        BackendAuth auth = new BackendAuthImpl();
        long groupId = GetterUtil.getLong(header.getHeaderString(Field.GROUP_ID));
        if (!auth.isAuth(serviceContext)) {
            return Response.status(HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION).entity(MessageUtil.getMessage(ConstantUtils.API_JSON_MESSAGE_NONAUTHORATIVE))
                    .build();
        }

        ErrorMsg error = new ErrorMsg();
        String[] idArr = ids.split(",");
        for (String idStr : idArr) {
            long id = Long.parseLong(idStr);
            FileStoreGov fileStoreGov = FileStoreGovLocalServiceUtil.fetchFileStoreGov(id);
            Response fileStoreGovValidator = FileStoreGovValidator.validateActions(id, user.getUserId(), fileStoreGov, serviceContext);
            if (fileStoreGovValidator.getStatus() != HttpStatus.SC_OK) {
                return Response.status(fileStoreGovValidator.getStatus())
                        .entity(fileStoreGovValidator.getEntity()).build();
            }
            if (Validator.isNotNull(fileStoreGov)) {
                try {
                    DLAppLocalServiceUtil.deleteFileEntry(fileStoreGov.getFileEntryId());
                    FileStoreGovLocalServiceUtil.deleteFileStoreGov(fileStoreGov);
                } catch (Exception e) {
                    _log.debug(e);
                    error.setCode(400);
                    error.setMessage(e.getMessage());
                    return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(error).build();
                }
            } else {
                error.setMessage("Not found fileStoreGov");
                error.setCode(HttpURLConnection.HTTP_BAD_REQUEST);
                return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(error).build();
            }
        }
        error.setCode(200);
        error.setMessage("Delete fileStoreGov successfully");
        return Response.status(HttpURLConnection.HTTP_OK).entity(error).build();
    }

    @Override
    public Response update(HttpServletRequest request, HttpHeaders header, Company company, Locale locale,
                           User user, ServiceContext serviceContext, long id, FileStoreGovInputModel model) {
        BackendAuth auth = new BackendAuthImpl();
        long groupId = GetterUtil.getLong(header.getHeaderString(Field.GROUP_ID));
        if (!auth.isAuth(serviceContext)) {
            return Response.status(HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION).entity(MessageUtil.getMessage(ConstantUtils.API_JSON_MESSAGE_NONAUTHORATIVE))
                    .build();
        }

        FileStoreGov fileStoreGovModel = FileStoreGovLocalServiceUtil.fetchFileStoreGov(id);
        Response fileStoreGovValidator = FileStoreGovValidator.validateActions(id, user.getUserId(), fileStoreGovModel, serviceContext);
        if (fileStoreGovValidator.getStatus() != HttpStatus.SC_OK) {
            return Response.status(fileStoreGovValidator.getStatus())
                    .entity(fileStoreGovValidator.getEntity()).build();
        }

        if (Validator.isNotNull(fileStoreGovModel)) {
            fileStoreGovModel.setFileStoreGovId(id);
            fileStoreGovModel.setGroupId(fileStoreGovModel.getGroupId());
            fileStoreGovModel.setCreateDate(fileStoreGovModel.getCreateDate());
            fileStoreGovModel.setModifiedDate(DateTimeUtil.getDateNow());
            // GovAgencyCode
            if (Validator.isNull(model.getGovAgencyCode())) {
                fileStoreGovModel.setGovAgencyCode(fileStoreGovModel.getGovAgencyCode());
            } else {
                fileStoreGovModel.setGovAgencyCode(model.getGovAgencyCode());
            }
            // ServiceCode
            if (Validator.isNull(model.getServiceCode())) {
                fileStoreGovModel.setServiceCode(fileStoreGovModel.getServiceCode());
            } else {
                fileStoreGovModel.setServiceCode(model.getServiceCode());
            }
            // FileEntryId
            if (Validator.isNull(model.getFileEntryId())) {
                fileStoreGovModel.setFileEntryId(fileStoreGovModel.getFileEntryId());
            } else {
                fileStoreGovModel.setFileEntryId(Long.parseLong(model.getFileEntryId()));
            }
            // FileGovCode
            if (Validator.isNull(model.getFileGovCode())) {
                fileStoreGovModel.setFileGovCode(fileStoreGovModel.getFileGovCode());
            } else {
                fileStoreGovModel.setFileGovCode(model.getFileGovCode());
            }
            // OwnerType
            if (Validator.isNull(model.getOwnerType())) {
                fileStoreGovModel.setOwnerType(fileStoreGovModel.getOwnerType());
            } else {
                fileStoreGovModel.setOwnerType(model.getOwnerType());
            }
            // OwnerNo
            if (Validator.isNull(model.getOwnerNo())) {
                fileStoreGovModel.setOwnerNo(fileStoreGovModel.getOwnerNo());
            } else {
                fileStoreGovModel.setOwnerNo(model.getOwnerNo());
            }
            // OwnerName
            if (Validator.isNull(model.getOwnerName())) {
                fileStoreGovModel.setOwnerName(fileStoreGovModel.getOwnerName());
            } else {
                fileStoreGovModel.setOwnerName(model.getOwnerName());
            }
            // OwnerDate
            if (Validator.isNull(model.getOwnerDate())) {
                fileStoreGovModel.setOwnerDate(fileStoreGovModel.getOwnerDate());
            } else {
                fileStoreGovModel.setOwnerDate(model.getOwnerDate());
            }
            // PartNo
            if (Validator.isNull(model.getPartNo())) {
                fileStoreGovModel.setPartNo(fileStoreGovModel.getPartNo());
            } else {
                fileStoreGovModel.setPartNo(model.getPartNo());
            }
            // FileName
            if (Validator.isNull(model.getFileName())) {
                fileStoreGovModel.setFileName(fileStoreGovModel.getFileName());
            } else {
                fileStoreGovModel.setFileName(model.getFileName());
            }
            // DisplayName
            if (Validator.isNull(model.getDisplayName())) {
                fileStoreGovModel.setDisplayName(fileStoreGovModel.getDisplayName());
            } else {
                fileStoreGovModel.setDisplayName(model.getDisplayName());
            }
            // CodeNumber
            if (Validator.isNull(model.getCodeNumber())) {
                fileStoreGovModel.setCodeNumber(fileStoreGovModel.getCodeNumber());
            } else {
                fileStoreGovModel.setCodeNumber(model.getCodeNumber());
            }
            // CodeNotation
            if (Validator.isNull(model.getCodeNotation())) {
                fileStoreGovModel.setCodeNotation(fileStoreGovModel.getCodeNotation());
            } else {
                fileStoreGovModel.setCodeNotation(model.getCodeNotation());
            }
            // DepartmentIssue
            if (Validator.isNull(model.getDepartmentIssue())) {
                fileStoreGovModel.setDepartmentIssue(fileStoreGovModel.getDepartmentIssue());
            } else {
                fileStoreGovModel.setDepartmentIssue(model.getDepartmentIssue());
            }
            // AbstractSS
            if (Validator.isNull(model.getAbstractSS())) {
                fileStoreGovModel.setAbstractSS(fileStoreGovModel.getAbstractSS());
            } else {
                fileStoreGovModel.setAbstractSS(model.getAbstractSS());
            }
            // ValidTo
            if (Validator.isNull(model.getValidTo())) {
                fileStoreGovModel.setValidTo(fileStoreGovModel.getValidTo());
            } else {
                fileStoreGovModel.setValidTo(APIDateTimeUtils._stringToDate(model.getValidTo(), DateTimeUtil._VN_DATE_FORMAT));
            }
            // ValidScope
            if (Validator.isNull(model.getValidScope())) {
                fileStoreGovModel.setValidScope(fileStoreGovModel.getValidScope());
            } else {
                fileStoreGovModel.setValidScope(model.getValidScope());
            }
            // FullInfo
            fileStoreGovModel.setFullInfo(fileStoreGovModel.getFullInfo());
            // Size_
            if (Validator.isNull(model.getSize_())) {
                fileStoreGovModel.setSize_(fileStoreGovModel.getSize_());
            } else {
                fileStoreGovModel.setSize_(Long.parseLong(model.getSize_()));
            }
            // PartType
            if (Validator.isNull(model.getPartType())) {
                fileStoreGovModel.setPartType(fileStoreGovModel.getPartType());
            } else {
                fileStoreGovModel.setPartType(Long.parseLong(model.getPartType()));
            }
            // PartTypeDetail
            if (Validator.isNull(model.getPartTypeDetail())) {
                fileStoreGovModel.setPartTypeDetail(fileStoreGovModel.getPartTypeDetail());
            } else {
                fileStoreGovModel.setPartTypeDetail(Long.parseLong(model.getPartTypeDetail()));
            }
            // Shared
            if (Validator.isNull(model.getShared())) {
                fileStoreGovModel.setShared(fileStoreGovModel.getShared());
            } else {
                fileStoreGovModel.setShared(Integer.parseInt(model.getShared()));
            }
            // IsActive
            if (Validator.isNull(model.getIsActive())) {
                fileStoreGovModel.setIsActive(Integer.parseInt(model.getIsActive()));
            } else {
                fileStoreGovModel.setIsActive(fileStoreGovModel.getIsActive());
            }
            String fileGovCode = generateFileGovCode(fileStoreGovModel.getOwnerNo(),
                    fileStoreGovModel.getOwnerName(), fileStoreGovModel.getOwnerDate(), fileStoreGovModel.getServiceCode(),
                    fileStoreGovModel.getPartNo(), Integer.valueOf(String.valueOf(fileStoreGovModel.getPartType())),
                    fileStoreGovModel.getCodeNumber(), fileStoreGovModel.getCodeNotation(), fileStoreGovModel.getDossierNo());

            fileStoreGovModel.setFileGovCode(fileGovCode);
            String typeNo = generateTypeNo(Integer.parseInt((String.valueOf(fileStoreGovModel.getPartType()))), fileStoreGovModel.getPartNo(), fileStoreGovModel.getServiceCode());
            fileStoreGovModel.setTypeNo(typeNo);
            fileStoreGovModel = FileStoreGovLocalServiceUtil.updateFileStoreGov(fileStoreGovModel);

            JSONObject searchResult = JSONFactoryUtil.createJSONObject();
            searchResult.put(org.opencps.dossiermgt.action.util.ConstantUtils.DATA, fileStoreGovModel);
            searchResult.put(org.opencps.dossiermgt.action.util.ConstantUtils.TOTAL, 1);
            return Response.status(HttpURLConnection.HTTP_OK).entity(searchResult.toJSONString()).build();

        } else {
            ErrorMsg error = new ErrorMsg();
            error.setMessage(MessageUtil.getMessage(ConstantUtils.API_MESSAGE_NOTFOUND));
            error.setCode(HttpURLConnection.HTTP_NOT_FOUND);
            error.setDescription(MessageUtil.getMessage(ConstantUtils.API_MESSAGE_NOTFOUND));
            return Response.status(HttpURLConnection.HTTP_NOT_FOUND).entity(error).build();
        }
    }

    private Response isValidFileStoreForUpdate(FileStoreGov fileStoreGov) {
        if (fileStoreGov == null) {
            return Response.status(HttpStatus.SC_BAD_REQUEST)
                    .entity(MessageUtil.getMessage(ConstantUtils.CANNOT_FIND_FILE))
                    .build();
        }
        if (fileStoreGov.getIsActive() == FileStoreActiveStatus.NOT_SIGNED_YET.getValue()) {
            return Response.status(HttpStatus.SC_OK).build();
        }
        if (fileStoreGov.getIsActive() != FileStoreActiveStatus.ACTIVE.getValue()) {
            return Response.status(HttpStatus.SC_BAD_REQUEST)
                    .entity(MessageUtil.getMessage(ConstantUtils.CANNOT_EDIT_DELETED_FILE))
                    .build();
        }
        return Response.status(HttpStatus.SC_OK).build();
    }

    private void saveToOtherDictItem(String itemName, long fileStoreGovId, long employeeId) {
        if (checkOtherDictItem(itemName).isEmpty()) {
            long id = CounterLocalServiceUtil.increment(OtherDictItem.class.getName());
            OtherDictItem otherDictItem = OtherDictItemLocalServiceUtil.createOtherDictItem(id);
            otherDictItem.setCreateDate(new Date());
            otherDictItem.setModifiedDate(new Date());
            otherDictItem.setFileStoreGovId(fileStoreGovId);
            otherDictItem.setEmployeeId(employeeId);
            otherDictItem.setItemName(itemName);
            OtherDictItemLocalServiceUtil.addOtherDictItem(otherDictItem);
        }
    }

    @Override
    public Response updateFileStoreGov(HttpServletRequest request, HttpHeaders header, Company company, Locale locale, User user, ServiceContext serviceContext, long id,
                                       Attachment file, String newFileEntryId, String serviceCode, String ownerType, String ownerNo, String ownerName, String ownerDate, String partNo, String fileName, String displayName, String codeNumber,
                                       String codeNotation, String departmentIssue, String otherDepartmentIssue, String otherFileName, String abstractSS, String partType, String partTypeDetail, String validTo, String validScope, String fullInfo, String issueDate, String isActive, String shared, String govAgencyCode,
                                       String dossierNo, String dossierName) {

        BackendAuth auth = new BackendAuthImpl();
        long groupId = GetterUtil.getLong(header.getHeaderString(Field.GROUP_ID));
        if (!auth.isAuth(serviceContext)) {
            return Response.status(HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION).entity(MessageUtil.getMessage(ConstantUtils.API_JSON_MESSAGE_NONAUTHORATIVE))
                    .build();
        }

        backend.auth.api.BackendAuth auth2 = new backend.auth.api.BackendAuthImpl();

        if (Validator.isNotNull(govAgencyCode) && !auth2.isAdmin(serviceContext, ConstantUtils.ROLE_ADMIN_LOWER)) {
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(ApiResponseUtils.fail("Chỉ admin được phép cập nhật govAgencyCode!")).build();
        }

        Date now = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String strDate = dateFormat.format(now);
        FileStoreGov fileStoreGovModel = FileStoreGovLocalServiceUtil.fetchFileStoreGov(id);
//        Response fileStoreGovValidator = FileStoreGovValidator.validateActions(id, user.getUserId(), fileStoreGovModel, serviceContext);
//        if (fileStoreGovValidator.getStatus() != HttpStatus.SC_OK) {
//            return Response.status(fileStoreGovValidator.getStatus())
//                    .entity(fileStoreGovValidator.getEntity()).build();
//        }
        Response validFileStoreGov = isValidFileStoreForUpdate(fileStoreGovModel);
        if (validFileStoreGov.getStatus() != HttpStatus.SC_OK) {
            return validFileStoreGov;
        }
//        JSONObject validateDossierNo = DossierManagementImpl.isValidDossierNo(dossierNo, fileStoreGovModel.getEmployeeId());
//        boolean validDossierNo = validateDossierNo.getBoolean("valid");
//        if (!validDossierNo) {
//            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(validateDossierNo.get("message")).build();
//        }
        FileEntry newFileEntry;
        try {
            if (Validator.isNotNull(fileStoreGovModel)) {
                if (Validator.isNotNull(file)) {
                    long oldFileEntryId = fileStoreGovModel.getFileEntryId();
                    newFileEntry = FileUploadUtils.uploadDossierFile(user.getUserId(), groupId, file.getDataHandler().getInputStream(), file.getDataHandler().getName(), null, 0, serviceContext);
                    if (Validator.isNotNull(newFileEntry)) {
                        fileStoreGovModel.setFileEntryId(newFileEntry.getFileEntryId());
                        fileStoreGovModel.setSize_(newFileEntry.getSize());
                        fileStoreGovModel.setDisplayName(newFileEntry.getFileName());
                        DLAppLocalServiceUtil.deleteFileEntry(oldFileEntryId);
                    }
                }
                if (Validator.isNotNull(newFileEntryId)) {
                    long oldFileEntryId = fileStoreGovModel.getFileEntryId();
                    newFileEntry = DLAppLocalServiceUtil.getFileEntry(Long.parseLong(newFileEntryId));
                    if (Validator.isNotNull(newFileEntry) && newFileEntry.getFileEntryId() != oldFileEntryId) {
                        fileStoreGovModel.setFileEntryId(newFileEntry.getFileEntryId());
                        fileStoreGovModel.setSize_(newFileEntry.getSize());
                        fileStoreGovModel.setDisplayName(newFileEntry.getFileName());
                        DLAppLocalServiceUtil.deleteFileEntry(oldFileEntryId);
                    }
                }
                fileStoreGovModel.setFileStoreGovId(id);
                fileStoreGovModel.setGroupId(fileStoreGovModel.getGroupId());
                fileStoreGovModel.setCreateDate(fileStoreGovModel.getCreateDate());
                fileStoreGovModel.setModifiedDate(DateTimeUtil.getDateNow());

                JSONObject fullInfoFileStoreGov = JSONFactoryUtil.createJSONObject();

                // GovAgencyCode
                if (Validator.isNotNull(govAgencyCode) && !govAgencyCode.isEmpty()) {
                    fileStoreGovModel.setGovAgencyCode(govAgencyCode);
                }
                // ServiceCode
                if (Validator.isNotNull(serviceCode)) {
                    fileStoreGovModel.setServiceCode(serviceCode);
                    fullInfoFileStoreGov.put("serviceCode", serviceCode);
                }
                // FileGovCode
//                if(Validator.isNull(model.getFileGovCode())){
//                    fileStoreGovModel.setFileGovCode(fileStoreGovModel.getFileGovCode());
//                }else {
//                    fileStoreGovModel.setFileGovCode(model.getFileGovCode());
//                }
                // OwnerType
                if (Validator.isNotNull(ownerType)) {
                    fileStoreGovModel.setOwnerType(ownerType);
                    fullInfoFileStoreGov.put("ownerType", ownerType);
                }
                // OwnerNo
                if (Validator.isNotNull(ownerNo)) {
                    fileStoreGovModel.setOwnerNo(ownerNo);
                    fullInfoFileStoreGov.put("ownerNo", ownerNo);
                }
                // OwnerName
                if (Validator.isNotNull(ownerName)) {
                    fileStoreGovModel.setOwnerName(ownerName);
                    fullInfoFileStoreGov.put("ownerName", ownerName);
                }
                // OwnerDate
                if (Validator.isNotNull(ownerDate)) {
                    fileStoreGovModel.setOwnerDate(ownerDate);
                    fullInfoFileStoreGov.put("ownerDate", ownerDate);
                }
                // PartNo
                if (Validator.isNotNull(partNo)) {
                    fileStoreGovModel.setPartNo(partNo);
                    fullInfoFileStoreGov.put("partNo", partNo);
                }
                // DisplayName
                if (Validator.isNotNull(displayName)) {
                    fileStoreGovModel.setDisplayName(displayName);
                }
                // CodeNumber
                if (Validator.isNotNull(codeNumber)) {
                    fileStoreGovModel.setCodeNumber(codeNumber);
                    fullInfoFileStoreGov.put("codeNumber", codeNumber);
                }
                // CodeNotation
                if (Validator.isNotNull(codeNotation)) {
                    fileStoreGovModel.setCodeNotation(codeNotation);
                    fullInfoFileStoreGov.put("codeNotation", codeNotation);
                }
                // DepartmentIssue
                if (Validator.isNotNull(otherDepartmentIssue) && !otherDepartmentIssue.equals(fileStoreGovModel.getDepartmentIssue())) {
                    fileStoreGovModel.setDepartmentIssue(otherDepartmentIssue);
                    fullInfoFileStoreGov.put("departmentIssue", otherDepartmentIssue);
                } else if (Validator.isNotNull(departmentIssue) && !departmentIssue.equals(fileStoreGovModel.getDepartmentIssue())) {
                    fileStoreGovModel.setDepartmentIssue(departmentIssue);
                    fullInfoFileStoreGov.put("departmentIssue", departmentIssue);
                }

                // fileName
                if (Validator.isNotNull(otherFileName) && !otherFileName.equals(fileStoreGovModel.getFileName())) {
                    fileStoreGovModel.setFileName(otherFileName);
                    fullInfoFileStoreGov.put("fileName", otherFileName);
                } else if (Validator.isNotNull(fileName) && !fileName.equals(fileStoreGovModel.getFileName())) {
                    fileStoreGovModel.setFileName(fileName);
                    fullInfoFileStoreGov.put("fileName", fileName);
                }
                // AbstractSS
                if (Validator.isNotNull(abstractSS)) {
                    fileStoreGovModel.setAbstractSS(abstractSS);
                    fullInfoFileStoreGov.put("abstractSS", abstractSS);
                }
                // ValidTo
                if (Validator.isNotNull(validTo)) {
                    fileStoreGovModel.setValidTo(APIDateTimeUtils._stringToDate(validTo, DateTimeUtil._VN_DATE_FORMAT));
                    fullInfoFileStoreGov.put("validTo", validTo);
                }
                // issueDate
                if (Validator.isNotNull(issueDate)) {
                    fileStoreGovModel.setIssueDate(APIDateTimeUtils._stringToDate(issueDate, DateTimeUtil._VN_DATE_FORMAT));
                    fullInfoFileStoreGov.put("issueDate", issueDate);
                }
                // ValidScope
                if (Validator.isNotNull(validScope)) {
                    fileStoreGovModel.setValidScope(validScope);
                    fullInfoFileStoreGov.put("validScope", validScope);
                }

                // PartType
                if (Validator.isNotNull(partType) && !StringUtils.isEmpty(partType)) {
                    fileStoreGovModel.setPartType(Long.parseLong(partType));
                }
                // PartTypeDetail
                if (Validator.isNotNull(partTypeDetail) && !StringUtils.isEmpty(partTypeDetail)) {
                    fileStoreGovModel.setPartTypeDetail(Long.parseLong(partTypeDetail));
                }
                // Shared
                if (Validator.isNotNull(shared) && !StringUtils.isEmpty(shared)) {
                    fileStoreGovModel.setShared(Integer.parseInt(shared));
                }
                // IsActive
                if (Validator.isNotNull(isActive) && !StringUtils.isEmpty(isActive)) {
                    fileStoreGovModel.setIsActive(Integer.parseInt(isActive));
                }

//                // FullInfo
//                if(Validator.isNotNull(fullInfo)){
//                    fileStoreGovModel.setFullInfo(fullInfo);
//                }

                fileStoreGovModel.setFullInfo(fullInfoFileStoreGov.toString());

                String fileGovCode = generateFileGovCode(fileStoreGovModel.getOwnerNo(),
                        fileStoreGovModel.getOwnerName(), fileStoreGovModel.getOwnerDate(), fileStoreGovModel.getServiceCode(),
                        fileStoreGovModel.getPartNo(), Integer.valueOf(String.valueOf(fileStoreGovModel.getPartType())),
                        fileStoreGovModel.getCodeNumber(), fileStoreGovModel.getCodeNotation(), fileStoreGovModel.getDossierNo());


                fileStoreGovModel.setFileGovCode(fileGovCode);
                String typeNo = generateTypeNo(Integer.parseInt((String.valueOf(fileStoreGovModel.getPartType()))), fileStoreGovModel.getPartNo(), fileStoreGovModel.getServiceCode());
                fileStoreGovModel.setTypeNo(typeNo);

                if (Validator.isNotNull(dossierNo)) {
                    dossierNo = dossierNo.trim();
                    fileStoreGovModel.setDossierNo(dossierNo);
                }
                if (Validator.isNotNull(dossierName))
                    fileStoreGovModel.setDossierName(dossierName);


                FileStoreGovLocalServiceUtil.updateFileStoreGov(fileStoreGovModel);
                long fileGovUsedHistoryId = CounterLocalServiceUtil.increment(FileGovUsedHistory.class.getName());
                _log.info("updateFileStoreGov:: fileGovUsedHistoryId = " + fileGovUsedHistoryId);
                FileGovUsedHistory fileGovUsedHistory = FileGovUsedHistoryLocalServiceUtil.createFileGovUsedHistory(fileGovUsedHistoryId);
                _log.info("updateFileStoreGov:: fileGovUsedHistory after updating = " + fileGovUsedHistory);
                fileGovUsedHistory.setFileStoreGovId(id);
                fileGovUsedHistory.setGroupId(groupId);
                if (Validator.isNotNull(newFileEntryId)) {
                    fileGovUsedHistory.setActionSS("Giấy tờ được thêm mới vào kho dữ liệu");
                } else {
                    fileGovUsedHistory.setActionSS("Cập nhật thông tin giấy tờ");
                }
                fileGovUsedHistory.setUserId(user.getUserId());
                fileGovUsedHistory.setCreateDate(APIDateTimeUtils._stringToDate(strDate, "dd/MM/yyyy HH:mm:ss"));

                FileGovUsedHistory fileGovUsedHistoryAfterUpdating = FileGovUsedHistoryLocalServiceUtil.updateFileGovUsedHistory(fileGovUsedHistory);
                _log.info("updateFileStoreGov:: fileGovUsedHistoryAfterUpdating  = " + fileGovUsedHistoryAfterUpdating);

            } else {
                return Response.status(org.apache.commons.httpclient.util.HttpURLConnection.HTTP_BAD_REQUEST).entity("Không tìm thấy file để update.").build();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(org.apache.commons.httpclient.util.HttpURLConnection.HTTP_BAD_REQUEST).entity("Có lỗi xảy ra trong quá trình xử lý").build();
        }
        if (Validator.isNotNull(otherDepartmentIssue)) {
            saveToOtherDictItem(otherDepartmentIssue, fileStoreGovModel.getFileStoreGovId(), fileStoreGovModel.getEmployeeId());
        }
        if (Validator.isNotNull(otherFileName)) {
            saveToOtherDictItem(otherFileName, fileStoreGovModel.getFileStoreGovId(), fileStoreGovModel.getEmployeeId());
        }
        return Response.status(HttpURLConnection.HTTP_OK).entity(ObjectConverterUtil.objectToJSON(FileStoreGovModel.class, fileStoreGovModel).toJSONString()).build();

    }

    private String formatOwnerName(String ownerName) {

        String temp = Normalizer.normalize(ownerName.replaceAll(" ", ""), Normalizer.Form.NFD);

        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String result = pattern.matcher(temp).replaceAll("");

        result = result.replaceAll("Đ", "D").replaceAll("đ", "d")
                .replaceAll("ă", "a").replaceAll("Ă", "A")
                .replaceAll("â", "a").replaceAll("Â", "a")
                .replaceAll("ô", "o").replaceAll("Ô", "o")
                .replaceAll("ơ", "o").replaceAll("Ơ", "O")
                .replaceAll("ư", "u").replaceAll("Ư", "u")
                .replaceAll("ê", "e").replaceAll("Ê", "e");

        result = result.toUpperCase();

        return result;
    }

    private String formatFileGovCode(String ownerNo, String partType, String codeNumber, String codeNotation, String serviceCode, String partNo) {
        if (partType.equals(String.valueOf(DossierPartType.ADMINISTRATIVE_FORMALITIES_RESULT.getValue()))) {
            List<String> sensitiveNames = Arrays.asList(SpecialCharacterUtils.SENSITIVE_NAMES);
            if (ownerNo == null || ownerNo.isEmpty() || sensitiveNames.contains(ownerNo)) {
                return partType + "." + codeNumber + codeNotation;
            }
            return ownerNo + "." + partType + "." + codeNumber + codeNotation;
        }
        return serviceCode + "." + partNo;
    }

    // [VIEW]
    public Response viewFile(HttpServletRequest request, HttpHeaders header, Company company, Locale locale, User user, ServiceContext serviceContext, long fileEntryId) {
        try {
            Response.ResponseBuilder responseBuilder;
            if (fileEntryId > 0) {
                FileEntry fileEntry = DLAppLocalServiceUtil.getFileEntry(fileEntryId);
                if (Validator.isNull(fileEntry)) {
                    responseBuilder = Response.status(Response.Status.BAD_REQUEST).entity("Không có file trên hệ thống");
                } else {
                    File file = DLFileEntryLocalServiceUtil.getFile(
                            fileEntry.getFileEntryId(), fileEntry.getVersion(), true);
                    responseBuilder = Response.ok(file);
                    String attachmentFilename = String.format(MessageUtil.getMessage(ConstantUtils.ATTACHMENT_FILENAME), fileEntry.getFileName());
                    responseBuilder.header(ConstantUtils.CONTENT_DISPOSITION, attachmentFilename);
                    responseBuilder.header(HttpHeaders.CONTENT_TYPE, fileEntry.getMimeType());
                }
                return responseBuilder.build();
            } else {
                responseBuilder = Response.status(Response.Status.BAD_REQUEST).entity("Không có file trên hệ thống");
                return responseBuilder.build();
            }
        } catch (Exception e) {
            return BusinessExceptionImpl.processException(e);
        }
    }

    //    @Override
    public Response getCapacity(HttpServletRequest request, HttpHeaders header,
                                Company company, Locale locale, User user, ServiceContext serviceContext) {
        BackendAuth auth = new BackendAuthImpl();
        if (!auth.isAuth(serviceContext)) {
            return Response.status(HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION).entity(MessageUtil.getMessage(ConstantUtils.API_JSON_MESSAGE_NONAUTHORATIVE))
                    .build();
        }
        JSONObject response = JSONFactoryUtil.createJSONObject();
        long groupId = GetterUtil.getLong(header.getHeaderString(Field.GROUP_ID));
        Employee employee = EmployeeLocalServiceUtil.fetchByFB_MUID(user.getUserId());
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
        if (Validator.isNotNull(employee)) {
            if (Validator.isNotNull(employee.getScope())) {
                DictCollection dictCollection = DictCollectionLocalServiceUtil.fetchByF_dictCollectionCode(GOVERNMENT_AGENCY, groupId);
                if (Validator.isNotNull(dictCollection)) {
                    DictItem dictItem = DictItemLocalServiceUtil.fetchByF_dictItemCode(employee.getScope(), dictCollection.getDictCollectionId(), groupId);
                    String scopeList = null;
                    if (Validator.isNotNull(dictItem)) {
                        scopeList = ScopeUtils.getAllSubUnit(dictItem, dictCollection.getDictCollectionId(), groupId);
                    }
                    params.put(FileStoreGovTerm.EMPLOYEE_SCOPE, scopeList);
                }

            }
        }
        params.put(Field.GROUP_ID, String.valueOf(groupId));
        params.put(FileStoreGovTerm.IS_ACTIVE, String.valueOf(1));
        params.put(FileStoreGovTerm.FILE_SOURCE, String.valueOf(0));
        Hits hits = null;
        SearchContext searchContext = new SearchContext();
        searchContext.setCompanyId(company.getCompanyId());
        try {
            hits = FileStoreGovLocalServiceUtil.search(params, null, -1, -1, searchContext);
        } catch (SearchException | ParseException e) {
            e.printStackTrace();
        }
        float capacity = 0;
        if (Validator.isNotNull(hits)) {
            List<Document> documents = new ArrayList<>(hits.toList());
            for (Document document : documents) {
                String sizeStr = document.get(FileStoreGovTerm.SIZE);
                if (sizeStr != null && !sizeStr.isEmpty()) {
                    capacity += Long.parseLong(sizeStr);
                }
            }
        }
        response.put("capacity", capacity);
        return Response.status(HttpStatus.SC_OK).entity(response.toJSONString()).build();
    }

    @Override
    public Response getUsageCount(HttpServletRequest request, HttpHeaders header, Company company, Locale locale, User user, ServiceContext serviceContext) {
        BackendAuth auth = new BackendAuthImpl();
        long groupId = GetterUtil.getLong(header.getHeaderString(Field.GROUP_ID));
        if (!auth.isAuth(serviceContext)) {
            return Response.status(HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION).entity(MessageUtil.getMessage(ConstantUtils.API_JSON_MESSAGE_NONAUTHORATIVE))
                    .build();
        }
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
        params.put(Field.GROUP_ID, String.valueOf(groupId));
        Employee employee = EmployeeLocalServiceUtil.fetchByFB_MUID(user.getUserId());
        if (Validator.isNotNull(employee)) {
            if (Validator.isNotNull(employee.getScope())) {
                DictCollection dictCollection = DictCollectionLocalServiceUtil.fetchByF_dictCollectionCode(GOVERNMENT_AGENCY, groupId);
                if (Validator.isNotNull(dictCollection)) {
                    DictItem dictItem = DictItemLocalServiceUtil.fetchByF_dictItemCode(employee.getScope(), dictCollection.getDictCollectionId(), groupId);
                    _log.debug("search:: dictItem = " + dictItem);
                    String scopeList = null;
                    if (Validator.isNotNull(dictItem)) {
                        scopeList = ScopeUtils.getAllSubUnit(dictItem, dictCollection.getDictCollectionId(), groupId);
                        _log.debug("search:: scopeList = " + scopeList);
                    }
                    if (scopeList != null) {
                        params.put(FileStoreGovTerm.GOVAGENCYCODE, SpecialCharacterUtils.splitSpecialNoComma(scopeList));
                    } else {
                        params.put(FileStoreGovTerm.GOVAGENCYCODE, employee.getScope());
                    }
                }
            }
        }
        params.put(FileStoreTerm.IS_ACTIVE, "1");
//        params.put(FileStoreGovTerm.PARTTYPE, DossierPartType.ADMINISTRATIVE_FORMALITIES_RESULT.getValue());
        JSONObject response = JSONFactoryUtil.createJSONObject();
        int total = 0;
        Hits hits = null;
        long usageCount = 0L;
        SearchContext searchContext = new SearchContext();
        searchContext.setCompanyId(company.getCompanyId());
        Sort[] sorts;
        String dateSort = String.format(MessageUtil.getMessage(ConstantUtils.QUERY_NUMBER_SORT), FileStoreTerm.CREATE_DATE);
        sorts = new Sort[]{SortFactoryUtil.create(dateSort, Sort.LONG_TYPE,
                GetterUtil.getBoolean(true))
        };
        try {
            hits = FileStoreGovLocalServiceUtil.search(params, sorts, -1, -1, searchContext);
        } catch (SearchException | ParseException e) {
            e.printStackTrace();
        }
        if (Validator.isNotNull(hits)) {
            List<Document> documents = new ArrayList<>(hits.toList());
            for (Document document : documents) {
                usageCount += getUsageCount(document);
            }
            total = hits.getLength();

        }
        response.put("total", total);
        response.put("usageCount", usageCount);

        return Response.status(HttpStatus.SC_OK).entity(response.toJSONString()).build();
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static String generateTypeNo(
            Integer partType,
            String partNo,
            String serviceCode
    ) {
        if (partType == null) {
            throw new IllegalArgumentException("partType must not be null");
        }

        switch (partType) {
            case 2:
                if (isBlank(partNo)) {
                    throw new IllegalArgumentException("partNo is required when partType = 2");
                }
                return partNo;

            case 1:
            case 8:
            case 9:
            case 19:
                if (isBlank(serviceCode)) {
                    throw new IllegalArgumentException("serviceCode is required when partType = " + partType);
                }
                if (isBlank(partNo)) {
                    throw new IllegalArgumentException("partNo is required when partType = 2");
                }
                return serviceCode + "." + partNo;

            default:
                throw new IllegalArgumentException("Unsupported partType: " + partType);
        }
    }

    public String generateFileGovCode(
            String ownerNo,
            String ownerName,
            String ownerDate,
            String serviceCode,
            String partNo,
            Integer partType,
            String codeNumber,
            String codeNotation,
            String dossierNo
    ) {

        // =====================
        // 1. ORG PART
        // =====================
        String orgPart;
        if (!isBlank(ownerNo)) {
            orgPart = ownerNo;
        } else {
            if (isBlank(ownerName)) {
                throw new IllegalArgumentException("ownerName is required when ownerNo is null/empty");
            }
            String dateStr = !isBlank(ownerName)
                    ? ownerDate
                    : "UNKNOWN_DATE";
            orgPart = ownerName + "." + dateStr;
        }

        // =====================
        // 2. DOC TYPE PART
        // =====================
        String docTypePart;
        if (partType == null) {
            throw new IllegalArgumentException("partType must not be null");
        }

        switch (partType) {
            case 2:
                if (isBlank(partNo)) {
                    throw new IllegalArgumentException("partNo is required when partType = 2");
                }
                docTypePart = partNo;
                break;

            case 1:
            case 8:
            case 9:
            case 19:
                if (isBlank(serviceCode)) {
                    throw new IllegalArgumentException("serviceCode is required when partType = " + partType);
                }
                if (isBlank(partNo)) {
                    throw new IllegalArgumentException("partNo is required when partType = " + partType);
                }
                docTypePart = serviceCode + "." + partNo;
                break;

            default:
                // Không throw nữa, fallback theo dữ liệu có sẵn
                if (!isBlank(serviceCode) && !isBlank(partNo)) {
                    docTypePart = serviceCode + "." + partNo;
                } else if (!isBlank(partNo)) {
                    docTypePart = partNo;
                } else {
                    docTypePart = "UNKNOWN_TYPE";
                }
                _log.info("generateFileGovCode: unsupported partType=" + partType + ", fallback docTypePart=" + docTypePart);
                break;
        }

        // =====================
        // 3. DOC NO PART
        // =====================
        String docNoPart;
        if (!isBlank(codeNumber) || !isBlank(codeNotation)) {
            String number = isBlank(codeNumber) ? "" : codeNumber;
            String notation = isBlank(codeNotation) ? "" : codeNotation;

            docNoPart = number + (number.isEmpty() || notation.isEmpty() ? "" : ".") + notation;
        } else {
            if (isBlank(dossierNo)) {
                throw new IllegalArgumentException("dossierNo is required when codeNumber & codeNotation are empty");
            }
            docNoPart = dossierNo;
        }

        // =====================
        // FINAL
        // =====================
        return orgPart + "." + docTypePart + "." + docNoPart;
    }

    @Override
    public Response doExportFileStoreGov(HttpServletRequest request, HttpHeaders header, Company company, Locale locale, User user, ServiceContext serviceContext, String govAgencyCode, String from, String to) throws IOException, JSONException, java.text.ParseException {
        return null;
    }

    private void handleExportFileStoreGovForNormalUser(Hits hits, LinkedHashMap<String, Object> params, Sort[] sorts,
                                                       SearchContext searchContext, Map<String, Object[]> summaryMap,
                                                       User user, Sheet detailSheet, int total, int usageCount) {
        try {
            hits = FileStoreGovLocalServiceUtil.search(params, sorts, -1, -1, searchContext);
        } catch (SearchException | ParseException e) {
            e.printStackTrace();
        }
        int detailRowNum = 4;
        List<Document> documents;

        if (Validator.isNotNull(hits)) {
            documents = new ArrayList<>(hits.toList());

            for (Document document : documents) {
                String departmentIssue = document.get("departmentIssue");

                Object[] data = summaryMap.getOrDefault(departmentIssue, new Object[]{0, 0L});
                int departmentTotal = (int) data[0];
                long departmentUsageCount = (long) data[1];

                departmentTotal++;

                List<FileGovUsedHistory> list = FileGovUsedHistoryLocalServiceUtil.getBysearchByFSGA(
                        Long.parseLong(document.get("entryClassPK")), "Tái sử dụng");
                List<FileGovUsedHistory> authenUsedFiles = list.stream()
                        .filter(file -> file.getUserId() == user.getUserId())
                        .collect(Collectors.toList());
                departmentUsageCount += authenUsedFiles.size();

                summaryMap.put(departmentIssue, new Object[]{departmentTotal, departmentUsageCount});

                total++;
                usageCount += authenUsedFiles.size();

                String codeNumber = document.get("codeNumber");
                String codeNotation = document.get("codeNotation");
                String fileName = document.get("fileName");
                String displayName = document.get("displayName");

                Row dataRowDetail = detailSheet.createRow(detailRowNum++);
                dataRowDetail.createCell(0).setCellValue(detailRowNum - 4);
                dataRowDetail.createCell(1).setCellValue(codeNumber + " " + codeNotation);
                dataRowDetail.createCell(2).setCellValue(fileName);
                dataRowDetail.createCell(3).setCellValue(displayName);
                dataRowDetail.createCell(4).setCellValue(departmentIssue);
            }
        }
    }

    private void handleExportFileStoreGovForAdmin(Map<String, Object[]> summaryMap, Sheet detailSheet, int totalForAll) {
        JSONArray statisticDepartments = statisticDepartments(null);
        JSONArray allFileStoreGov = getAllFileStoreGov(null);
        int detailRowNum = 4;
        for (int i = 0; i < Objects.requireNonNull(statisticDepartments).length(); i++) {
            JSONObject obj = statisticDepartments.getJSONObject(i);
            String departmentIssue = obj.getString("departmentissue");
            String total = obj.getString("total");
            totalForAll += Integer.parseInt(total);
            String totalUsage = obj.getString("totalUsage");
            summaryMap.put(departmentIssue, new Object[]{total, totalUsage});
        }

        for (int i = 0; i < Objects.requireNonNull(allFileStoreGov).length(); i++) {
            JSONObject obj = allFileStoreGov.getJSONObject(i);
            String codeNumber = obj.getString("codenumber");
            String codeNotation = obj.getString("codenotation");
            String fileName = obj.getString("fileName");
            String displayName = obj.getString("displayName");
            String departmentIssue = obj.getString("departmentIssue");

            Row dataRowDetail = detailSheet.createRow(detailRowNum++);
            dataRowDetail.createCell(0).setCellValue(detailRowNum - 4);
            dataRowDetail.createCell(1).setCellValue(codeNumber + codeNotation);
            dataRowDetail.createCell(2).setCellValue(fileName);
            dataRowDetail.createCell(3).setCellValue(displayName);
            dataRowDetail.createCell(4).setCellValue(departmentIssue);
        }
    }

    private Sheet templateSheetForAllGovsByLevel(XSSFWorkbook workbook, String govAgencyCode, DictCollection dictCollection,
                                                 long groupId, Employee employee, String from, String to, String[] headersForSheetAllGovsByLevel) {
        Sheet sheetAllGovsByLevel = workbook.createSheet("Tổng hợp đơn vị cấp cha");
        Row titleRow = sheetAllGovsByLevel.createRow(0);
        CellStyle titleCellStyle = workbook.createCellStyle();
        XSSFFont titleFont = workbook.createFont();
        titleFont.setFontHeight(16);
        titleFont.setBold(true);
        titleCellStyle.setFont(titleFont);
        titleCellStyle.setWrapText(true);
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        Cell titleCell = titleRow.createCell(0);
        DictItem item = null;
        if (Validator.isNotNull(govAgencyCode)) {
            item = DictItemLocalServiceUtil.fetchByF_dictItemCode(govAgencyCode, dictCollection.getDictCollectionId(), groupId);
            titleCell.setCellValue("Thống kê tình hình số hóa hồ sơ cơ quan " + item.getItemName());
        } else {
            titleCell.setCellValue("Thống kê tình hình số hóa hồ sơ cơ quan " + employee.getFullName());
        }

        titleCell.setCellStyle(titleCellStyle);
        titleRow.setHeight((short) 1000);
        sheetAllGovsByLevel.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        CellStyle subTitleCellStyle = workbook.createCellStyle();
        subTitleCellStyle.setAlignment(HorizontalAlignment.RIGHT);
        XSSFFont dataFont = workbook.createFont();
        dataFont.setFontHeight(12);
        subTitleCellStyle.setFont(dataFont);

        Row subTitleRow1 = sheetAllGovsByLevel.createRow(1);
        Cell subTitleCell1 = subTitleRow1.createCell(0);
        subTitleCell1.setCellValue("Dữ liệu thông kê trong khoảng thời gian: " + from + " - " + to);
        subTitleCell1.setCellStyle(subTitleCellStyle);
        subTitleRow1.setHeight((short) 600);
        sheetAllGovsByLevel.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));

        DateTimeFormatter localDateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
        LocalDateTime today = LocalDateTime.now();
        Row subTitleRow2 = sheetAllGovsByLevel.createRow(2);
        Cell subTitleCell2 = subTitleRow2.createCell(0);
        subTitleCell2.setCellValue("Dữ liệu báo cáo được tổng hợp vào : " + today.format(localDateFormatter));
        subTitleCell2.setCellStyle(subTitleCellStyle);
        subTitleRow2.setHeight((short) 600);
        sheetAllGovsByLevel.addMergedRegion(new CellRangeAddress(2, 2, 0, 3));


        CellStyle headerCellStyle = workbook.createCellStyle();
        XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeight(12);
        headerCellStyle.setFont(headerFont);
        Row headerRow = sheetAllGovsByLevel.createRow(3);
        headerRow.setHeight((short) 600);

        for (int i = 0; i < headersForSheetAllGovsByLevel.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headersForSheetAllGovsByLevel[i]);
            cell.setCellStyle(headerCellStyle);
            sheetAllGovsByLevel.autoSizeColumn(i);
        }
        sheetAllGovsByLevel.createFreezePane(0, 3);
        return sheetAllGovsByLevel;
    }

    private Sheet templateSheetForAllGovsAll(XSSFWorkbook workbook, String govAgencyCode, DictCollection dictCollection,
                                             long groupId, Employee employee, String from, String to, String[] headersForSheetAllGovsByLevel) {
        Sheet sheetGovsAll = workbook.createSheet("Tổng hợp các đơn vị cấp con");
        Row titleRow = sheetGovsAll.createRow(0);
        CellStyle titleCellStyle = workbook.createCellStyle();
        XSSFFont titleFont = workbook.createFont();
        titleFont.setFontHeight(16);
        titleFont.setBold(true);
        titleCellStyle.setFont(titleFont);
        titleCellStyle.setWrapText(true);
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        Cell titleCell = titleRow.createCell(0);
        DictItem item = null;
        if (Validator.isNotNull(govAgencyCode)) {
            item = DictItemLocalServiceUtil.fetchByF_dictItemCode(govAgencyCode, dictCollection.getDictCollectionId(), groupId);
            titleCell.setCellValue("Thống kê tình hình số hóa hồ sơ cơ quan " + item.getItemName());
        } else {
            titleCell.setCellValue("Thống kê tình hình số hóa hồ sơ cơ quan " + employee.getFullName());
        }

        titleCell.setCellStyle(titleCellStyle);
        titleRow.setHeight((short) 1000);
        sheetGovsAll.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        CellStyle subTitleCellStyle = workbook.createCellStyle();
        subTitleCellStyle.setAlignment(HorizontalAlignment.RIGHT);
        XSSFFont dataFont = workbook.createFont();
        dataFont.setFontHeight(12);
        subTitleCellStyle.setFont(dataFont);

        Row subTitleRow1 = sheetGovsAll.createRow(1);
        Cell subTitleCell1 = subTitleRow1.createCell(0);
        subTitleCell1.setCellValue("Dữ liệu thông kê trong khoảng thời gian: " + from + " - " + to);
        subTitleCell1.setCellStyle(subTitleCellStyle);
        subTitleRow1.setHeight((short) 600);
        sheetGovsAll.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));

        DateTimeFormatter localDateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
        LocalDateTime today = LocalDateTime.now();
        Row subTitleRow2 = sheetGovsAll.createRow(2);
        Cell subTitleCell2 = subTitleRow2.createCell(0);
        subTitleCell2.setCellValue("Dữ liệu báo cáo được tổng hợp vào : " + today.format(localDateFormatter));
        subTitleCell2.setCellStyle(subTitleCellStyle);
        subTitleRow2.setHeight((short) 600);
        sheetGovsAll.addMergedRegion(new CellRangeAddress(2, 2, 0, 3));


        CellStyle headerCellStyle = workbook.createCellStyle();
        XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeight(12);
        headerCellStyle.setFont(headerFont);
        Row headerRow = sheetGovsAll.createRow(3);
        headerRow.setHeight((short) 600);

        for (int i = 0; i < headersForSheetAllGovsByLevel.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headersForSheetAllGovsByLevel[i]);
            cell.setCellStyle(headerCellStyle);
            sheetGovsAll.autoSizeColumn(i);
        }
        sheetGovsAll.createFreezePane(0, 3);
        return sheetGovsAll;
    }

    private Sheet templateForDetailSheet(XSSFWorkbook workbook, String[] detailHeaders) {
        CellStyle titleCellStyle = workbook.createCellStyle();
        Sheet detailSheet = workbook.createSheet("Thống kê chi tiết");
        Row titleRowDetail = detailSheet.createRow(0);
        titleRowDetail.setHeight((short) 1000);
        Cell titleCellDetail = titleRowDetail.createCell(0);
        titleCellDetail.setCellValue("THỐNG KÊ TÌNH HÌNH SỐ HÓA HỒ SƠ CHI TIẾT");
        titleCellDetail.setCellStyle(titleCellStyle);
        detailSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));


        CellStyle subTitleCellStyle = workbook.createCellStyle();
        subTitleCellStyle.setAlignment(HorizontalAlignment.RIGHT);
        XSSFFont dataFont = workbook.createFont();
        dataFont.setFontHeight(12);
        subTitleCellStyle.setFont(dataFont);

        CellStyle headerCellStyle = workbook.createCellStyle();
        XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeight(12);
        headerCellStyle.setFont(headerFont);


        Row dateRowDetail = detailSheet.createRow(1);
        Cell dateCellDetail = dateRowDetail.createCell(0);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime today = LocalDateTime.now();
        dateCellDetail.setCellValue("Ngày xuất báo cáo: " + today.format(dateFormatter));
        dateCellDetail.setCellStyle(subTitleCellStyle);
        dateRowDetail.setHeight((short) 600);
        detailSheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));

        Row emptyRowDetail = detailSheet.createRow(2);
        emptyRowDetail.setHeight((short) 600);

        Row headerRowDetail = detailSheet.createRow(3);
        headerRowDetail.setHeight((short) 600);
        for (int i = 0; i < detailHeaders.length; i++) {
            Cell cell = headerRowDetail.createCell(i);
            cell.setCellValue(detailHeaders[i]);
            cell.setCellStyle(headerCellStyle);
        }

        detailSheet.setColumnWidth(0, 256 * 10);
        detailSheet.setColumnWidth(1, 256 * 30);
        detailSheet.setColumnWidth(2, 256 * 80);
        detailSheet.setColumnWidth(3, 256 * 65);
        detailSheet.setColumnWidth(4, 256 * 65);

        CellStyle customStyle2 = workbook.createCellStyle();
        for (int rowIndex = 0; rowIndex <= 3; rowIndex++) {
            Row row = detailSheet.getRow(rowIndex);
            if (row != null) {
                for (int colIndex = 0; colIndex < detailHeaders.length; colIndex++) {
                    Cell cell = row.getCell(colIndex);
                    if (cell == null) {
                        cell = row.createCell(colIndex);
                    }

                    CellStyle existingStyle = cell.getCellStyle();
                    customStyle2.cloneStyleFrom(existingStyle);

                    XSSFFont timesNewRomanFont = workbook.createFont();
                    timesNewRomanFont.setFontName("Times New Roman");
                    customStyle2.setFont(timesNewRomanFont);

                    if (rowIndex == 0) {
                        timesNewRomanFont.setFontHeight(16);
                        timesNewRomanFont.setBold(true);
                    } else if (rowIndex == 3) {
                        timesNewRomanFont.setBold(true);

                        if (colIndex == 0) {
                            customStyle2.setAlignment(HorizontalAlignment.CENTER);
                        }
                        customStyle2.setBorderTop(BorderStyle.THIN);
                        customStyle2.setBorderBottom(BorderStyle.THIN);
                        customStyle2.setBorderLeft(BorderStyle.THIN);
                        customStyle2.setBorderRight(BorderStyle.THIN);
                    }

                    cell.setCellStyle(customStyle2);
                }
            }
        }
        CellStyle newStyle2 = workbook.createCellStyle();
        XSSFFont xssfFont2 = workbook.createFont();
        for (int rowIndex = 4; rowIndex <= detailSheet.getLastRowNum(); rowIndex++) {
            Row row = detailSheet.getRow(rowIndex);
            if (row != null) {
                for (int colIndex = 0; colIndex < detailHeaders.length; colIndex++) {
                    Cell cell = row.getCell(colIndex);
                    if (cell == null) {
                        cell = row.createCell(colIndex);
                    }
                    CellStyle existingStyle = cell.getCellStyle();
                    boolean isSTTColumn = colIndex == 0; // Kiểm tra nếu là cột STT
                    CellStyle updatedStyle = createUpdatedStyle(existingStyle, newStyle2, xssfFont2, true, isSTTColumn);
                    cell.setCellStyle(updatedStyle);
                }
            }
        }
        return detailSheet;
    }

    @Override
    public Response exportFileStoreGovExcel(HttpServletRequest request, HttpHeaders header,
                                            Company company, Locale locale, User user, ServiceContext serviceContext, FileStoreGovSearchModel fileStoreGovSearchModel) throws JSONException, SearchException, ParseException, IOException {
        Long groupId = GetterUtil.getLong(header.getHeaderString(Field.GROUP_ID));

        // Kiểm tra thông tin employee và quyền admin
        Employee employee = EmployeeLocalServiceUtil.fetchByFB_MUID(user.getUserId());
        backend.auth.api.BackendAuth auth = new backend.auth.api.BackendAuthImpl();
        if (Validator.isNull(employee) && !auth.isAdmin(serviceContext, ConstantUtils.ROLE_ADMIN_LOWER)) {
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity("Cannot find employee information").build();
        }
        String govAgencyCode = "";
        if (auth.isAdmin(serviceContext, ConstantUtils.ROLE_ADMIN_LOWER)) {
            govAgencyCode = "000.00.00.G11";
        }

        // Chuẩn bị tham số tìm kiếm
        LinkedHashMap<String, Object> params = prepareSearchParams(groupId, employee);

        // Khởi tạo workbook và các sheet
        XSSFWorkbook workbook = new XSSFWorkbook();
        String[] headersForSheetAllGovsByLevel = {"STT", "Tên đơn vị", "Số lượng hồ sơ đã số hóa", "Số giấy tờ đã số hóa", "Số lần sử dụng giấy tờ"};
        String[] detailHeaders = {"STT", "Số, Ký hiệu", "Tên giấy tờ", "Mã hồ sơ", "Lĩnh vực", "Thủ tục hành chính", "Tệp tin", "Cơ quan cập nhật giấy tờ"};

        DictCollection dictCollection = DictCollectionLocalServiceUtil.fetchByF_dictCollectionCode(GOVERNMENT_AGENCY, groupId);
        Sheet sheetAllGovsByLevel = null;
        if (auth.isAdmin(serviceContext, ConstantUtils.ROLE_ADMIN_LOWER) || employee.getScope().equalsIgnoreCase("000.00.00.G11")) {
            sheetAllGovsByLevel = templateSheetForAllGovsByLevel(workbook, govAgencyCode, dictCollection, groupId, employee, "", "", headersForSheetAllGovsByLevel);
        }
        Sheet sheetAllGovsAll = templateSheetForAllGovsAll(workbook, govAgencyCode, dictCollection, groupId, employee, "", "", headersForSheetAllGovsByLevel);
        Sheet detailSheet = templateForDetailSheet(workbook, detailHeaders);

        // Xử lý dữ liệu thống kê
        Map<String, Object[]> summaryMapByLevel = new LinkedHashMap<>();
        Map<String, Object[]> summaryMapAll = new LinkedHashMap<>();
        int[] totalsByLevel = {0, 0, 0}; // [total, usageCount, dossierNoCount]
        int[] totalsGovAll = {0, 0, 0};  // [total, usageCount, dossierNoCount]

        processStatistics(auth, serviceContext, employee, params, company, summaryMapByLevel, summaryMapAll, detailSheet, totalsByLevel, totalsGovAll, fileStoreGovSearchModel, groupId);

        if (!summaryMapByLevel.isEmpty() && sheetAllGovsByLevel != null) {
            writeSummaryToSheet(sheetAllGovsByLevel, summaryMapByLevel, totalsByLevel, headersForSheetAllGovsByLevel.length);
            applyStylesAndAutoSize(workbook, sheetAllGovsByLevel, headersForSheetAllGovsByLevel);
        }
        if (!summaryMapAll.isEmpty()) {
            writeSummaryToSheet(sheetAllGovsAll, summaryMapAll, totalsGovAll, headersForSheetAllGovsByLevel.length);
            applyStylesAndAutoSize(workbook, sheetAllGovsAll, headersForSheetAllGovsByLevel);
            applyStylesAndAutoSize(workbook, detailSheet, detailHeaders);
        }

        // Xuất file
        return exportFile(workbook, user, company, groupId, serviceContext);
    }

    private LinkedHashMap<String, Object> prepareSearchParams(Long groupId, Employee employee) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put(Field.GROUP_ID, String.valueOf(groupId));
        params.put(FileStoreTerm.IS_ACTIVE, "1");

        if (Validator.isNotNull(employee) && Validator.isNotNull(employee.getScope())) {
            DictCollection dictCollection = DictCollectionLocalServiceUtil.fetchByF_dictCollectionCode(GOVERNMENT_AGENCY, groupId);
            if (Validator.isNotNull(dictCollection)) {
                DictItem dictItem = DictItemLocalServiceUtil.fetchByF_dictItemCode(employee.getScope(), dictCollection.getDictCollectionId(), groupId);
                if (Validator.isNotNull(dictItem)) {
                    String scopeList = ScopeUtils.getAllSubUnit(dictItem, dictCollection.getDictCollectionId(), groupId);
                    params.put(FileStoreGovTerm.GOVAGENCYCODE, scopeList);
                } else {
                    params.put(FileStoreGovTerm.GOVAGENCYCODE, employee.getScope());
                }
            }
        }
        return params;
    }


    private void processStatistics(backend.auth.api.BackendAuth auth, ServiceContext serviceContext, Employee employee,
                                   LinkedHashMap<String, Object> params, Company company,
                                   Map<String, Object[]> summaryMapByLevel, Map<String, Object[]> summaryMapAll,
                                   Sheet detailSheet, int[] totalsByLevel, int[] totalsGovAll, FileStoreGovSearchModel fileStoreGovSearchModel, Long groupId) throws JSONException, SearchException, ParseException {
        if (auth.isAdmin(serviceContext, ConstantUtils.ROLE_ADMIN_LOWER) || employee.getScope().equalsIgnoreCase("000.00.00.G11")) {
            String employeeScope = "000.00.00.G11";
            processAdminStatistics2(summaryMapByLevel, summaryMapAll, detailSheet, totalsByLevel, totalsGovAll, employeeScope, fileStoreGovSearchModel, groupId);
        } else {
            processUserStatistics3(summaryMapByLevel, summaryMapAll, params, detailSheet, totalsByLevel, totalsGovAll, employee, fileStoreGovSearchModel, groupId);
        }
    }


//    private void processStatistics(backend.auth.api.BackendAuth auth, ServiceContext serviceContext, Employee employee,
//                                   LinkedHashMap<String, Object> params, Company company,
//                                   Map<String, Object[]> summaryMapByLevel, Map<String, Object[]> summaryMapAll,
//                                   Sheet detailSheet, int[] totalsByLevel, int[] totalsGovAll) throws JSONException, SearchException, ParseException {
//        if (auth.isAdmin(serviceContext, ConstantUtils.ROLE_ADMIN_LOWER) || employee.getScope().equalsIgnoreCase("000.00.00.G11")) {
//            String employeeScope = "000.00.00.G11";

    /// /            processAdminStatistics(summaryMapByLevel, summaryMapAll, detailSheet, totalsByLevel, totalsGovAll, employeeScope);
//            processAdminStatistics2(summaryMapByLevel, summaryMapAll, detailSheet, totalsByLevel, totalsGovAll, employeeScope);
//        } else {
//            processUserStatistics3(summaryMapByLevel, summaryMapAll, params, detailSheet, totalsByLevel, totalsGovAll, employee);
//        }
//    }
    public List<DictItem> sortDictItems(List<DictItem> dictItems, String employeeScope) {
        long groupId = 272638L;

        DictItem employeeDictItem = DictCollectionUtils.getDictItemByCode(
                DataMGTConstants.GOVERNMENT_AGENCY, employeeScope, groupId);
        if (employeeDictItem != null && dictItems.stream().noneMatch(di -> di.getItemCode().equals(employeeDictItem.getItemCode()))) {
            dictItems.add(employeeDictItem);
        }

        Map<String, DictItem> dictItemMap = dictItems.stream()
                .collect(Collectors.toMap(
                        DictItem::getItemCode,
                        Function.identity(),
                        (existing, replacement) -> existing
                ));
        for (DictItem dictItem : dictItems) {
            try {
                if (dictItem != null && !employeeScope.equals(dictItem.getItemCode()) && dictItem.getParentItemId() != 0) {
                    DictItem parentItem = DictItemLocalServiceUtil.getDictItem(dictItem.getParentItemId());
                    dictItemMap.put(parentItem.getItemCode(), parentItem);
                }
            } catch (PortalException e) {
//                throw new RuntimeException(e);
            }
        }
        dictItems = new ArrayList<>(dictItemMap.values());
        // Bước 1: Tạo map để nhóm các mục con theo parentDictItemId
        Map<Long, List<DictItem>> childrenMap = new HashMap<>();
        for (DictItem item : dictItems) {
            long parentId = item.getParentItemId();
            childrenMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(item);
        }

        // Bước 2: Tìm các mục gốc (level 0 hoặc parentDictItemId = 0)
        List<DictItem> roots = new ArrayList<>();
        for (DictItem item : dictItems) {
            if (item.getParentItemId() == 0 || item.getLevel_() == 0 || item.getItemCode().equalsIgnoreCase(employeeScope) || !employeeScope.equals(item.getItemCode())) {
                roots.add(item);
            }
        }

        // Sắp xếp các mục gốc (theo itemName hoặc tiêu chí khác nếu cần)
        roots.sort(Comparator.comparing(DictItem::getItemName));

        // Bước 3: Duyệt cây và thu thập kết quả
        List<DictItem> result = new ArrayList<>();
        for (DictItem root : roots) {
            collectItems(root, childrenMap, result);
        }

        return result;
    }

    private void collectItems(DictItem item, Map<Long, List<DictItem>> childrenMap, List<DictItem> result) {
        // Thêm mục hiện tại vào kết quả
        result.add(item);

        // Lấy danh sách con của mục hiện tại
        List<DictItem> children = childrenMap.getOrDefault(item.getDictItemId(), new ArrayList<>());

        // Sắp xếp con theo itemName (hoặc tiêu chí khác nếu cần)
        children.sort(Comparator.comparing(DictItem::getItemName));

        // Đệ quy cho từng con
        for (DictItem child : children) {
            collectItems(child, childrenMap, result);
        }
    }

    private void traverse(DictItem parent, Map<Long, List<DictItem>> map, List<DictItem> result) {
        result.add(parent); // Thêm cha vào kết quả

        List<DictItem> children = map.get(parent.getDictItemId());
        if (children != null) {
            // Sắp xếp các con nếu cần (ví dụ theo itemName)
            children.sort(Comparator.comparing(DictItem::getItemName));
            for (DictItem child : children) {
                traverse(child, map, result); // đệ quy cho từng con
            }
        }
    }

    private void processAdminStatistics(Map<String, Object[]> summaryMapByLevel, Map<String, Object[]> summaryMapAll,
                                        Sheet detailSheet, int[] totalsByLevel, int[] totalsGovAll, String employeeScope) throws JSONException {
        List<DictItem> dictItems = statisticDictItem(null);
        List<DictItem> sortDictItems = new ArrayList<>();
        if (dictItems != null) {
            sortDictItems = sortDictItems(dictItems, employeeScope);
        }
        Map<Long, List<DictItem>> tree = tree(sortDictItems);
        Map<String, JSONObject> directFileCount = statisticGovAgencyCode(null);

        JSONArray statisticGovsByLevel = calculateResult(sortDictItems, tree, directFileCount, 2, null);
        JSONArray allFileStoreGov = getAllFileStoreGov(null);
        JSONArray statisticGovsAll = statisticDepartments(null);
        JSONArray sortedStatisticGovsAll = sortItemCodes(sortDictItems, statisticGovsAll);
        int detailRowNum = 4;
        for (int i = 0; i < statisticGovsByLevel.length(); i++) {
            JSONObject obj = statisticGovsByLevel.getJSONObject(i);
            String govAgencyName = obj.getString(FileStoreGovTerm.GOVAGENCYNAME);
            int total = obj.getInt(FileStoreGovTerm.TOTAL);
            int usage = obj.getInt(FileStoreGovTerm.TOTAL_USAGE);
            int dossierNoCount = obj.getInt(FileStoreGovTerm.DOSSIERNO_COUNT);
            summaryMapByLevel.put(govAgencyName, new Object[]{total, usage, dossierNoCount});
            totalsByLevel[0] += total;
            totalsByLevel[1] += usage;
            totalsByLevel[2] += dossierNoCount;
        }

        for (int i = 0; i < allFileStoreGov.length(); i++) {
            JSONObject obj = allFileStoreGov.getJSONObject(i);
            Row row = detailSheet.createRow(detailRowNum++);
            row.createCell(0).setCellValue(detailRowNum - 4);
            row.createCell(1).setCellValue(obj.getString("codenumber") + obj.getString("codenotation"));
            row.createCell(2).setCellValue(obj.getString("fileName"));
            row.createCell(3).setCellValue(obj.getString("dossierNo"));
            row.createCell(4).setCellValue(obj.getString("displayName"));
            row.createCell(5).setCellValue(obj.getString("govAgencyName"));
        }

        for (int i = 0; i < sortedStatisticGovsAll.length(); i++) {
            JSONObject obj = sortedStatisticGovsAll.getJSONObject(i);
            String govAgencyCode = obj.getString(FileStoreGovTerm.GOVAGENCYCODE);
            DictItem dictItem = sortDictItems.stream().filter(di ->
                    di.getItemCode().equals(govAgencyCode)
            ).findAny().orElse(null);
            if (dictItem != null) {
                String govAgencyName = formatGovName(dictItem);
                int total = obj.getInt(FileStoreGovTerm.TOTAL);
                int usage = obj.getInt(FileStoreGovTerm.TOTAL_USAGE);
                int dossierNoCount = obj.getInt(FileStoreGovTerm.DOSSIERNO_COUNT);
                // Cộng dồn nếu đã có, nếu chưa có thì khởi tạo
                summaryMapAll.compute(govAgencyName, (k, prev) -> {
                    if (prev == null) return new Object[]{total, usage, dossierNoCount};
                    prev[0] = ((Number) prev[0]).intValue() + total;
                    prev[1] = ((Number) prev[1]).intValue() + usage;
                    prev[2] = ((Number) prev[2]).intValue() + dossierNoCount;
                    return prev;
                });

                // Tổng cộng toàn cục (giữ nguyên logic hiện tại)
                totalsGovAll[0] += total;
                totalsGovAll[1] += usage;
                totalsGovAll[2] += dossierNoCount;
            }
        }
    }

    private String formatGovName(DictItem dictItem) {
        String level2Format = "*  ";
        String level3Format = "*  *  ";
        String level4Format = "*  *  *  ";
        if (dictItem.getLevel_() == 2) {
            return level2Format + dictItem.getItemName();
        }
        if (dictItem.getLevel_() == 3) {
            return level3Format + dictItem.getItemName();
        }
        if (dictItem.getLevel_() == 4) {
            return level4Format + dictItem.getItemName();
        }
        return dictItem.getItemName();
    }

    public String transformScope(String input) {
        String[] parts = input.split(",");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
//            String modified = parts[i].replace("_", ".");
            result.append("'").append(parts[i]).append("'");
            if (i < parts.length - 1) {
                result.append(","); // Thêm dấu phẩy giữa các phần tử
            }
        }

        return result.toString();
    }

    private void processUserStatistics3(Map<String, Object[]> summaryMapByLevel, Map<String, Object[]> summaryMapAll, LinkedHashMap<String, Object> params,
                                        Sheet detailSheet, int[] totalsByLevel, int[] totalsGovAll, Employee employee, FileStoreGovSearchModel fileStoreGovSearchModel, Long groupId) {
        String scope = transformScope((String) params.get(FileStoreGovTerm.GOVAGENCYCODE));
        DictItem currentDictItem = DictCollectionUtils.getDictItemByCode(GOVERNMENT_AGENCY, employee.getScope(), MCDT_GROUP_ID);
        JSONArray allFileStoreGov = getAllFileStoreGovWithScope(scope, fileStoreGovSearchModel, groupId);
        JSONArray statisticFileStoreGovAll = statisticStatisticFilestoreGovDTOs(allFileStoreGov, currentDictItem, true);
        int detailRowNum = 4;
        for (int i = 0; i < allFileStoreGov.length(); i++) {
            JSONObject obj = allFileStoreGov.getJSONObject(i);
            Row row = detailSheet.createRow(detailRowNum++);
            row.createCell(0).setCellValue(detailRowNum - 4);
            row.createCell(1).setCellValue(obj.getString(FileStoreGovTerm.CODENUMBER) + obj.getString(FileStoreGovTerm.CODENOTATION));
            row.createCell(2).setCellValue(obj.getString(FileStoreGovTerm.FILENAME));
            row.createCell(3).setCellValue(obj.getString(FileStoreGovTerm.DOSSIER_NO));
            row.createCell(4).setCellValue(obj.getString(FileStoreGovTerm.DOMAIN_NAME));
            row.createCell(5).setCellValue(obj.getString(FileStoreGovTerm.SERVICE_NAME));
            row.createCell(6).setCellValue(obj.getString(FileStoreGovTerm.DISPLAYNAME));
            row.createCell(7).setCellValue(obj.getString(FileStoreGovTerm.GOVAGENCYNAME));
        }

        for (int i = 0; i < statisticFileStoreGovAll.length(); i++) {
            JSONObject obj = statisticFileStoreGovAll.getJSONObject(i);
            int total = obj.getInt(FileStoreGovTerm.TOTAL);
            int usage = obj.getInt(FileStoreGovTerm.TOTAL_USAGE);
            int dossierNoCount = obj.getInt(FileStoreGovTerm.DOSSIERNO_COUNT);
            String govAgencyName = obj.getString(FileStoreGovTerm.GOVAGENCYNAME);
            // Cộng dồn nếu đã có, nếu chưa có thì khởi tạo
            summaryMapAll.compute(govAgencyName, (k, prev) -> {
                if (prev == null) return new Object[]{total, usage, dossierNoCount};
                prev[0] = ((Number) prev[0]).intValue() + total;
                prev[1] = ((Number) prev[1]).intValue() + usage;
                prev[2] = ((Number) prev[2]).intValue() + dossierNoCount;
                return prev;
            });
            // Tổng cộng toàn cục (giữ nguyên logic hiện tại)
            totalsGovAll[0] += total;
            totalsGovAll[1] += usage;
            totalsGovAll[2] += dossierNoCount;
        }
    }

    private void processAdminStatistics2(Map<String, Object[]> summaryMapByLevel, Map<String, Object[]> summaryMapAll,
                                         Sheet detailSheet, int[] totalsByLevel, int[] totalsGovAll, String adminScope,
                                         FileStoreGovSearchModel fileStoreGovSearchModel, Long groupId) {
        DictItem currentDictItem = DictCollectionUtils.getDictItemByCode(GOVERNMENT_AGENCY, adminScope, MCDT_GROUP_ID);
        JSONArray allFileStoreGov = getAllFileStoreGovWithoutScope(fileStoreGovSearchModel, groupId);
        JSONArray statisticFileStoreGovAll = statisticStatisticFilestoreGovDTOs(allFileStoreGov, currentDictItem, true);
        JSONArray statisticFileStoreGovLevel = statisticStatisticFilestoreGovDTOs(allFileStoreGov, currentDictItem, false);
        int detailRowNum = 4;
        for (int i = 0; i < allFileStoreGov.length(); i++) {
            JSONObject obj = allFileStoreGov.getJSONObject(i);
            Row row = detailSheet.createRow(detailRowNum++);
            row.createCell(0).setCellValue(detailRowNum - 4);
            row.createCell(1).setCellValue(obj.getString(FileStoreGovTerm.CODENUMBER) + obj.getString(FileStoreGovTerm.CODENOTATION));
            row.createCell(2).setCellValue(obj.getString(FileStoreGovTerm.FILENAME));
            row.createCell(3).setCellValue(obj.getString(FileStoreGovTerm.DOSSIER_NO));
            row.createCell(4).setCellValue(obj.getString(FileStoreGovTerm.DOMAIN_NAME));
            row.createCell(5).setCellValue(obj.getString(FileStoreGovTerm.SERVICE_NAME));
            row.createCell(6).setCellValue(obj.getString(FileStoreGovTerm.DISPLAYNAME));
            row.createCell(7).setCellValue(obj.getString(FileStoreGovTerm.GOVAGENCYNAME));
        }

        for (int i = 0; i < statisticFileStoreGovAll.length(); i++) {
            JSONObject obj = statisticFileStoreGovAll.getJSONObject(i);
            int total = obj.getInt(FileStoreGovTerm.TOTAL);
            int usage = obj.getInt(FileStoreGovTerm.TOTAL_USAGE);
            int dossierNoCount = obj.getInt(FileStoreGovTerm.DOSSIERNO_COUNT);
            String govAgencyName = obj.getString(FileStoreGovTerm.GOVAGENCYNAME);
            // Cộng dồn nếu đã có, nếu chưa có thì khởi tạo
            summaryMapAll.compute(govAgencyName, (k, prev) -> {
                if (prev == null) return new Object[]{total, usage, dossierNoCount};
                prev[0] = ((Number) prev[0]).intValue() + total;
                prev[1] = ((Number) prev[1]).intValue() + usage;
                prev[2] = ((Number) prev[2]).intValue() + dossierNoCount;
                return prev;
            });
            // Tổng cộng toàn cục (giữ nguyên logic hiện tại)
            totalsGovAll[0] += total;
            totalsGovAll[1] += usage;
            totalsGovAll[2] += dossierNoCount;
        }

        for (int i = 0; i < statisticFileStoreGovLevel.length(); i++) {
            JSONObject obj = statisticFileStoreGovLevel.getJSONObject(i);
            int total = obj.getInt(FileStoreGovTerm.TOTAL);
            int usage = obj.getInt(FileStoreGovTerm.TOTAL_USAGE);
            int dossierNoCount = obj.getInt(FileStoreGovTerm.DOSSIERNO_COUNT);
            String govAgencyName = obj.getString(FileStoreGovTerm.GOVAGENCYNAME);
            // Cộng dồn nếu đã có, nếu chưa có thì khởi tạo
            summaryMapByLevel.compute(govAgencyName, (k, prev) -> {
                if (prev == null) return new Object[]{total, usage, dossierNoCount};
                prev[0] = ((Number) prev[0]).intValue() + total;
                prev[1] = ((Number) prev[1]).intValue() + usage;
                prev[2] = ((Number) prev[2]).intValue() + dossierNoCount;
                return prev;
            });
            // Tổng cộng toàn cục (giữ nguyên logic hiện tại)
            totalsByLevel[0] += total;
            totalsByLevel[1] += usage;
            totalsByLevel[2] += dossierNoCount;
        }
    }

    public JSONArray statisticStatisticFilestoreGovDTOs(JSONArray allFileStoreGov, DictItem currentDictItem, boolean forAll) {
        // Tạo JSONArray để lưu kết quả
        JSONArray resultArray = JSONFactoryUtil.createJSONArray();

        // Bước 1: Xây dựng map các item độc đáo dựa trên DICTITEMID
        Map<Long, StatisticFilestoreGovDTO> itemMap = new HashMap<>();

        for (int i = 0; i < allFileStoreGov.length(); i++) {
            JSONObject obj = allFileStoreGov.getJSONObject(i);
            String itemName = obj.getString(FileStoreGovTerm.GOVAGENCYNAME, null);
            String itemCode = obj.getString(FileStoreGovTerm.GOVAGENCYCODE, null);
            int fileStoreGovId = obj.getInt(FileStoreGovTerm.FILESTOREGOVID, 0);


            // Bỏ qua nếu ITEMNAME null hoặc empty
            if (itemName == null || itemName.isEmpty()) {
                continue;
            }

            long dictItemId = obj.getLong(DictItemTerm.DICT_ITEM_ID, 0);
            if (dictItemId == 0) {
                continue; // Bỏ qua nếu DICTITEMID invalid
            }

            // Lấy hoặc tạo mới StatisticFilestoreGovDTO
            StatisticFilestoreGovDTO item = itemMap.computeIfAbsent(dictItemId, k -> {
                StatisticFilestoreGovDTO newItem = new StatisticFilestoreGovDTO();
                newItem.setDictItemId(dictItemId);
                newItem.setItemName(itemName);
                newItem.setItemCode(itemCode);
                newItem.setParentItemId(obj.getLong(DictItemTerm.PARENT_ITEM_ID, 0));
                newItem.setLevel(obj.getInt(DictItemTerm.LEVEL, 1)); // Mặc định level 1 nếu không có
                return newItem;
            });

            // Tăng fileCount
            item.setFileCount(item.getFileCount() + 1);
            item.setUsageCount(item.getUsageCount() + getUsageCount(fileStoreGovId));

            // Thêm DOSSIERNO độc đáo nếu có
            String dossierNo = obj.getString(DossierTerm.DOSSIER_NO, null);
            if (dossierNo != null && !dossierNo.isEmpty()) {
                item.addDossierNo(dossierNo);
            }
        }

        // Bước 2: Xây dựng cây (add children vào parent)
        buildTree(itemMap, currentDictItem);

        // Bước 3: Tìm roots (các item có parentItemId = 0)
        List<StatisticFilestoreGovDTO> roots = itemMap.values().stream()
                .filter(item -> item.getItemCode().equals(currentDictItem.getItemCode()))
                .sorted(Comparator.comparingInt((StatisticFilestoreGovDTO i) -> i.getLevel())
                        .thenComparing(Comparator.comparing((StatisticFilestoreGovDTO i) -> i.getItemName()).reversed()))
                .collect(Collectors.toList());

        if (roots == null || roots.isEmpty()) {
            roots = new ArrayList<>();
            StatisticFilestoreGovDTO root = new StatisticFilestoreGovDTO();
            root.setUsageCount(0);
            root.setDictItemId(currentDictItem.getDictItemId());
            root.setItemName(currentDictItem.getItemName());
            root.setParentItemId(currentDictItem.getParentItemId());
            root.setLevel(currentDictItem.getLevel_());
            root.setFileCount(0);
            root.setDossierNos(new HashSet<>());
            root.setChildren(new ArrayList<>(itemMap.values()));
            root.setItemCode(currentDictItem.getItemCode());
            roots.add(root);
        }

//            root.setDictItemId();
//            root.set


        // Bước 4: Traversal và thêm vào JSONArray
        for (StatisticFilestoreGovDTO root : roots) {
            if (forAll) {
                appendItemToArrayForAll1(root, resultArray, root.getLevel());
            } else {
                Set<String> visited = new HashSet<>();
                appendItemToArrayForLevelStatistic(root, resultArray, root.getLevel(), visited);
            }
        }

        return resultArray;
    }

    private void buildTree(Map<Long, StatisticFilestoreGovDTO> itemMap, DictItem currentDictItem) {
        final int currentLevel = currentDictItem.getLevel_();

        // snapshot để tránh ConcurrentModification khi thêm vào itemMap trong lúc duyệt
        for (StatisticFilestoreGovDTO startNode : new ArrayList<>(itemMap.values())) {

            StatisticFilestoreGovDTO child = startNode;
            long parentId = child.getParentItemId();

            // phòng vòng lặp do dữ liệu sai (chu kỳ cha-con)
            Set<Long> visited = new HashSet<>();

            while (parentId != 0 && visited.add(parentId)) {
                StatisticFilestoreGovDTO parentDto = itemMap.get(parentId);
                DictItem parentEntity = null;

                if (parentDto == null) {
                    try {
                        parentEntity = DictItemLocalServiceUtil.getDictItem(parentId);
                    } catch (PortalException e) {
                        throw new RuntimeException(e);
                    }

                    // Chỉ đưa vào map nếu level lớn hơn currentLevel
                    if (parentEntity.getLevel_() > currentLevel) {
                        parentDto = mappingFileStoreDTO(parentEntity, child);
                        itemMap.put(parentDto.getDictItemId(), parentDto);
                    }
                }

                // Nếu đã có/đã tạo được parentDto thì gắn quan hệ và leo tiếp
                if (parentDto != null) {
                    // tránh add trùng con
                    StatisticFilestoreGovDTO finalChild = child;
                    boolean exists = parentDto.getChildren()
                            .stream()
                            .anyMatch(c -> c.getDictItemId() == finalChild.getDictItemId());
                    if (!exists) {
                        parentDto.addChild(child);
                    }

                    // leo lên cha tiếp theo
                    child = parentDto;
                    parentId = parentDto.getParentItemId();
                } else {
                    // parent không được thêm vì không qua level filter → vẫn tiếp tục leo bằng entity
                    if (parentEntity == null) break; // an toàn
                    if (parentEntity.getItemCode().equals(currentDictItem.getItemCode())) break; // dừng tại scope
                    parentId = parentEntity.getParentItemId();
                }
            }
        }
    }

    private StatisticFilestoreGovDTO mappingFileStoreDTO(DictItem parentItem, StatisticFilestoreGovDTO child) {
        StatisticFilestoreGovDTO dto = new StatisticFilestoreGovDTO();
        dto.setItemCode(parentItem.getItemCode());
        dto.setLevel(parentItem.getLevel_());
        dto.setItemName(parentItem.getItemName());
        dto.setParentItemId(parentItem.getParentItemId());
        dto.setDictItemId(parentItem.getDictItemId());
        dto.addChild(child);
        return dto;
    }

    // Phương thức recursive để thêm item và children vào JSONArray
    private void appendItemToArrayForAll(StatisticFilestoreGovDTO item, JSONArray resultArray, int indentLevel) {
        // Tạo JSONObject cho item hiện tại
        JSONObject jsonItem = JSONFactoryUtil.createJSONObject();

        // Tạo indent bằng * (dựa trên level - 1, giả sử level 1 không có *)
        int count = Math.max(0, item.getLevel() - 1);
        String indent = String.join("", Collections.nCopies(count, "* "));
        jsonItem.put(FileStoreGovTerm.GOVAGENCYNAME, indent + item.getItemName());
        jsonItem.put(FileStoreGovTerm.TOTAL, item.getFileCount());
        jsonItem.put(FileStoreGovTerm.TOTAL_USAGE, item.getUsageCount());
        jsonItem.put(FileStoreGovTerm.DOSSIERNO_COUNT, item.getDossierNos().size());

        // Thêm vào JSONArray
        resultArray.put(jsonItem);

        // Sắp xếp children: level asc, rồi ITEMNAME desc
        item.getChildren().sort(Comparator.comparingInt((StatisticFilestoreGovDTO i) -> i.getLevel())
                .thenComparing(Comparator.comparing((StatisticFilestoreGovDTO i) -> i.getItemName()).reversed()));

        // Recursive cho children
        for (StatisticFilestoreGovDTO child : item.getChildren()) {
            appendItemToArrayForAll(child, resultArray, indentLevel + 1);
        }
    }

    private void appendItemToArrayForAll1(StatisticFilestoreGovDTO item, JSONArray resultArray, int indentLevel) {
        // Tạo indent bằng * (dựa trên level - 1, giả sử level 1 không có *)
        int count = Math.max(0, item.getLevel() - 1);
        String indent = String.join("", Collections.nCopies(count, "* "));

        String govAgencyName = indent + item.getItemName();

        // Check xem trong resultArray đã có object này chưa
        boolean exists = false;
        for (int i = 0; i < resultArray.length(); i++) {
            JSONObject existing = resultArray.getJSONObject(i);
            if (govAgencyName.equals(existing.getString(FileStoreGovTerm.GOVAGENCYNAME))) {
                exists = true;
                break;
            }
        }

        // Nếu chưa tồn tại thì mới thêm vào
        if (!exists) {
            JSONObject jsonItem = JSONFactoryUtil.createJSONObject();
            jsonItem.put(FileStoreGovTerm.GOVAGENCYNAME, govAgencyName);
            jsonItem.put(FileStoreGovTerm.TOTAL, item.getFileCount());
            jsonItem.put(FileStoreGovTerm.TOTAL_USAGE, item.getUsageCount());
            jsonItem.put(FileStoreGovTerm.DOSSIERNO_COUNT, item.getDossierNos().size());

            resultArray.put(jsonItem);
        }

        // Sắp xếp children: level asc, rồi ITEMNAME desc
        item.getChildren().sort(
                Comparator.comparingInt((StatisticFilestoreGovDTO i) -> i.getLevel())
                        .thenComparing(Comparator.comparing(
                                (StatisticFilestoreGovDTO i) -> i.getItemName()).reversed())
        );

        // Recursive cho children
        for (StatisticFilestoreGovDTO child : item.getChildren()) {
            appendItemToArrayForAll1(child, resultArray, indentLevel + 1);
        }
    }

    private void appendItemToArrayForLevelStatistic(
            StatisticFilestoreGovDTO item, JSONArray resultArray, int indentLevel, Set<String> visited) {

        // Nếu item này đã được duyệt rồi thì bỏ qua
        if (visited.contains(item.getItemName())) {
            return;
        }
        visited.add(item.getItemName());

        // Cộng dồn giá trị từ children vào item trước
        for (StatisticFilestoreGovDTO child : item.getChildren()) {
            appendItemToArrayForLevelStatistic(child, resultArray, indentLevel + 1, visited); // tính totals cho con
            // Cộng dồn vào cha
            item.setFileCount(item.getFileCount() + child.getFileCount());
            item.setUsageCount(item.getUsageCount() + child.getUsageCount());
            item.getDossierNos().addAll(child.getDossierNos());
        }

        // Chỉ thêm đơn vị cha vào resultArray (không thêm con riêng lẻ)
        if (item.getLevel() == 1) { // giả sử level=1 là đơn vị cha
            int count = Math.max(0, item.getLevel() - 1);
            String indent = String.join("", Collections.nCopies(count, "* "));

            JSONObject jsonItem = JSONFactoryUtil.createJSONObject();
            jsonItem.put(FileStoreGovTerm.GOVAGENCYNAME, indent + item.getItemName());
            jsonItem.put(FileStoreGovTerm.TOTAL, item.getFileCount());
            jsonItem.put(FileStoreGovTerm.TOTAL_USAGE, item.getUsageCount());
            jsonItem.put(FileStoreGovTerm.DOSSIERNO_COUNT, item.getDossierNos().size());

            resultArray.put(jsonItem);
        }
    }

    private void processUserStatistics2(Map<String, Object[]> summaryMapByLevel, Map<String, Object[]> summaryMapAll, LinkedHashMap<String, Object> params,
                                        Sheet detailSheet, int[] totalsByLevel, int[] totalsGovAll, String employeeScope) {
        String scope = transformScope((String) params.get(FileStoreGovTerm.GOVAGENCYCODE));
        List<DictItem> dictItems = statisticDictItem(scope);
        List<DictItem> sortDictItems = new ArrayList<>();
        if (dictItems != null) {
            sortDictItems = sortDictItems(dictItems, employeeScope);
        }
        Map<Long, List<DictItem>> tree = tree(sortDictItems);
        Map<String, JSONObject> directFileCount = statisticGovAgencyCode(scope);
        JSONArray statisticGovsByLevel = calculateResult(sortDictItems, tree, directFileCount, 5, scope);
        JSONArray allFileStoreGov = getAllFileStoreGov(scope);
        JSONArray statisticGovsAll = statisticDepartments(scope);
        JSONArray sortedStatisticGovsAll = sortItemCodes(sortDictItems, statisticGovsAll);
        int detailRowNum = 4;
        for (int i = 0; i < statisticGovsByLevel.length(); i++) {
            JSONObject obj = statisticGovsByLevel.getJSONObject(i);
            String govAgencyName = obj.getString(FileStoreGovTerm.GOVAGENCYNAME);
            int total = obj.getInt(FileStoreGovTerm.TOTAL);
            int usage = obj.getInt(FileStoreGovTerm.TOTAL_USAGE);
            int dossierNoCount = obj.getInt(FileStoreGovTerm.DOSSIERNO_COUNT);
            summaryMapByLevel.put(govAgencyName, new Object[]{total, usage, dossierNoCount});
            totalsByLevel[0] += total;
            totalsByLevel[1] += usage;
            totalsByLevel[2] += dossierNoCount;
        }

        for (int i = 0; i < allFileStoreGov.length(); i++) {
            JSONObject obj = allFileStoreGov.getJSONObject(i);
            Row row = detailSheet.createRow(detailRowNum++);
            row.createCell(0).setCellValue(detailRowNum - 4);
            row.createCell(1).setCellValue(obj.getString("codenumber") + obj.getString("codenotation"));
            row.createCell(2).setCellValue(obj.getString("fileName"));
            row.createCell(3).setCellValue(obj.getString("dossierNo"));
            row.createCell(4).setCellValue(obj.getString("displayName"));
            row.createCell(5).setCellValue(obj.getString("govAgencyName"));
        }

        for (int i = 0; i < sortedStatisticGovsAll.length(); i++) {
            JSONObject obj = sortedStatisticGovsAll.getJSONObject(i);
            String govAgencyCode = obj.getString(FileStoreGovTerm.GOVAGENCYCODE);
            DictItem dictItem = sortDictItems.stream().filter(di ->
                    di.getItemCode().equals(govAgencyCode)
            ).findAny().orElse(null);
            if (dictItem != null) {
                String govAgencyName = formatGovName(dictItem);
                int total = obj.getInt(FileStoreGovTerm.TOTAL);
                int usage = obj.getInt(FileStoreGovTerm.TOTAL_USAGE);
                int dossierNoCount = obj.getInt(FileStoreGovTerm.DOSSIERNO_COUNT);
                // Cộng dồn nếu đã có, nếu chưa có thì khởi tạo
                summaryMapAll.compute(govAgencyName, (k, prev) -> {
                    if (prev == null) return new Object[]{total, usage, dossierNoCount};
                    prev[0] = ((Number) prev[0]).intValue() + total;
                    prev[1] = ((Number) prev[1]).intValue() + usage;
                    prev[2] = ((Number) prev[2]).intValue() + dossierNoCount;
                    return prev;
                });

                // Tổng cộng toàn cục (giữ nguyên logic hiện tại)
                totalsGovAll[0] += total;
                totalsGovAll[1] += usage;
                totalsGovAll[2] += dossierNoCount;
            }
        }
    }


    private void processUserStatistics(LinkedHashMap<String, Object> params, Company company,
                                       Map<String, Object[]> summaryMapAll, Sheet detailSheet, int[] totalsGovAll, Employee employee) throws SearchException, ParseException {
        SearchContext searchContext = new SearchContext();
        searchContext.setCompanyId(company.getCompanyId());
        Sort[] sorts = {SortFactoryUtil.create("createDate", Sort.LONG_TYPE, true)};
        Hits hits = FileStoreGovLocalServiceUtil.search(params, sorts, -1, -1, searchContext);
        if (Validator.isNotNull(hits)) {
            int detailRowNum = 4;
            for (Document doc : hits.toList()) {
                String govAgencyCode = doc.get("govAgencyCode");
                DictCollection dictCollection = DictCollectionLocalServiceUtil.fetchByF_dictCollectionCode(GOVERNMENT_AGENCY, 272638);
                DictItem item = DictItemLocalServiceUtil.fetchByF_dictItemCode(govAgencyCode, dictCollection.getDictCollectionId(), 272638);
                if (item == null) {
                    continue;
                }
                Object[] data = summaryMapAll.computeIfAbsent(item.getItemName(), k -> new Object[]{0, 0});
                int total = (int) data[0] + 1;
                List<FileGovUsedHistory> authenUsedFiles = FileGovUsedHistoryLocalServiceUtil.getBysearchByFSGA(
                        Long.parseLong(doc.get("entryClassPK")), "Tái sử dụng");
//                        .filter(file -> file.getUserId() == employee.getMappingUserId())
//                        .collect(Collectors.toList());
                int usage = (int) data[1] + authenUsedFiles.size();
                summaryMapAll.put(item.getItemName(), new Object[]{total, usage});
                totalsGovAll[0]++;
                totalsGovAll[1] += authenUsedFiles.size();

                Row row = detailSheet.createRow(detailRowNum++);
                row.createCell(0).setCellValue(detailRowNum - 4);
                row.createCell(1).setCellValue(doc.get("codeNumber") + " " + doc.get("codeNotation"));
                row.createCell(2).setCellValue(doc.get("fileName"));
                row.createCell(3).setCellValue(doc.get("displayName"));
                row.createCell(4).setCellValue(item.getItemName());
            }
        }
    }

    private void writeSummaryToSheet(Sheet sheet, Map<String, Object[]> summaryMap, int[] totals, int columnCount) {
        Row totalRow = sheet.createRow(4);
        totalRow.createCell(0).setCellValue(1);
        totalRow.createCell(1).setCellValue("Tổng cộng");
        totalRow.createCell(2).setCellValue(totals[2]);
        totalRow.createCell(3).setCellValue(totals[0]);
        totalRow.createCell(4).setCellValue(totals[1]);

        int rowNum = 5;
        for (Map.Entry<String, Object[]> entry : summaryMap.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowNum - 4);
            row.createCell(1).setCellValue(entry.getKey());
            row.createCell(2).setCellValue((int) entry.getValue()[2]);
            row.createCell(3).setCellValue((int) entry.getValue()[0]);
            row.createCell(4).setCellValue((int) entry.getValue()[1]);
        }
    }

    public Response exportPdfFileStoreGov(HttpServletRequest request, HttpHeaders header, Company company, Locale locale, User user,
                                          ServiceContext serviceContext, FileStoreGovSearchModel fileStoreGovSearchModel) {
        BackendAuth auth = new BackendAuthImpl();
        if (!auth.isAuth(serviceContext)) {
            return Response.status(HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION).entity(new QLCDResponse(QLCDConstants.CODE_FAIL, "You are not authorized to perform this action")).build();
        }

        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        try {
            params = buildSearchParams(fileStoreGovSearchModel, user, serviceContext, header, company);
        } catch (NotFoundException e) {
            JSONObject searchResult = JSONFactoryUtil.createJSONObject();
            searchResult.put(ConstantUtils.DATA, new ArrayList<>());
            searchResult.put(ConstantUtils.TOTAL, 0);
            searchResult.put("usageCount", 0);
            searchResult.put("dossierNoCount", 0);
            return Response.status(HttpURLConnection.HTTP_OK).entity(searchResult.toJSONString()).build();
        } catch (UnauthorizationException e) {
            return Response.status(HttpStatus.SC_FORBIDDEN).entity(new QLCDResponse(QLCDConstants.CODE_FAIL, "Permission denied")).build();
        }

        long groupId = GetterUtil.getLong(header.getHeaderString(Field.GROUP_ID));
        Hits hits;
        SearchContext searchContext = new SearchContext();
        searchContext.setCompanyId(company.getCompanyId());
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        int total = 0;
        String sort = fileStoreGovSearchModel.getSort();

        Sort[] sorts;
        if ("1".equals(sort)) {
            String dateSort = String.format(MessageUtil.getMessage(ConstantUtils.QUERY_NUMBER_SORT), Field.MODIFIED_DATE);
            sorts = new Sort[]{SortFactoryUtil.create(dateSort, Sort.LONG_TYPE,
                    GetterUtil.getBoolean(true))
            };
        } else if ("dossierNo".equals(sort)) {
            String dossierNoSort = String.format(MessageUtil.getMessage(ConstantUtils.QUERY_STRING_SORT), FileStoreGovTerm.DOSSIER_NO);
            sorts = new Sort[]{SortFactoryUtil.create(dossierNoSort, Sort.STRING_TYPE,
                    GetterUtil.getBoolean(true))
            };
        } else {
            String dateSort = String.format(MessageUtil.getMessage(ConstantUtils.QUERY_NUMBER_SORT), Field.CREATE_DATE);
            sorts = new Sort[]{SortFactoryUtil.create(dateSort, Sort.LONG_TYPE,
                    GetterUtil.getBoolean(true))
            };
        }
        JSONArray resultArr = JSONFactoryUtil.createJSONArray();
        String[] resultHeaders = {"STT", "Số, Ký hiệu", "Tên giấy tờ", "Mã hồ sơ", "Lĩnh vực", "Thủ tục hành chính", "Tệp tin", "Cơ quan cập nhật giấy tờ"};
        try {
            hits = FileStoreGovLocalServiceUtil.search(params, sorts, -1, -1, searchContext);
            if (Validator.isNotNull(hits)) {
                List<Document> resultList = hits.toList();
                for (Document value : resultList) {
                    JSONObject result = JSONFactoryUtil.createJSONObject();
                    Document document = value;
                    result.put(FileStoreGovTerm.COMBINED_CODE, document.get(FileStoreGovTerm.CODENUMBER) + document.get(FileStoreGovTerm.CODENOTATION));
                    result.put(FileStoreGovTerm.FILENAME, document.get(FileStoreGovTerm.FILENAME));
                    result.put(FileStoreGovTerm.DOSSIER_NO, document.get(FileStoreGovTerm.DOSSIER_NO));
                    result.put(FileStoreGovTerm.SERVICECODE, document.get(FileStoreGovTerm.SERVICECODE));
                    result.put(FileStoreGovTerm.DISPLAYNAME, document.get(FileStoreGovTerm.DISPLAYNAME));
                    result.put(FileStoreGovTerm.GOVAGENCYNAME, document.get(FileStoreGovTerm.GOVAGENCYNAME));
                    resultArr.put(result);
                }
            }

        } catch (Exception e) {
            _log.error(e);
        }
        List<ServiceInfo> serviceInfoList = ServiceInfoLocalServiceUtil.getServiceInfosByGroupId(groupId, -1, -1);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            com.itextpdf.text.Document document = new com.itextpdf.text.Document(PageSize.A4.rotate(), 54, 36, 36, 36); // margins: left, right, top, bottom
            PdfWriter.getInstance(document, baos);
            document.open();

            // Load font as resource from classpath
            InputStream fontStream = getClass().getClassLoader().getResourceAsStream("font/DejaVuSans.ttf");
            if (fontStream == null) {
                throw new RuntimeException("Font resource not found");
            }
            File tempFile = File.createTempFile("DejaVuSans", ".ttf");
            tempFile.deleteOnExit();
            try (OutputStream out = Files.newOutputStream(tempFile.toPath())) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fontStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            BaseFont bf = BaseFont.createFont(tempFile.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            Font fontTitle = new Font(bf, 14);
            Font fontHeader = new Font(bf, 12);
            Font fontBody = new Font(bf, 10);

            // Title
            Paragraph title = new Paragraph("DANH SÁCH GIẤY TỜ", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Table setup
            float[] columnWidths = {1f, 2f, 3f, 3f, 2f, 4f, 2f, 4f};
            PdfPTable table = new PdfPTable(columnWidths);
            table.setWidthPercentage(100);

            // Add headers
            for (String headerText : resultHeaders) {
                PdfPCell headerCell = new PdfPCell(new Phrase(headerText, fontHeader));
                headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(headerCell);
            }

            table.setHeaderRows(1);

            // Add data rows
            for (int i = 0; i < resultArr.length(); i++) {
                JSONObject obj = resultArr.getJSONObject(i);

                String domainName = serviceInfoList.stream()
                        .filter(service -> service.getServiceCode().equals(obj.getString(FileStoreGovTerm.SERVICECODE)))
                        .findFirst()
                        .map(ServiceInfo::getDomainName)
                        .orElse("");

                String serviceName = serviceInfoList.stream()
                        .filter(service -> service.getServiceCode().equals(obj.getString(FileStoreGovTerm.SERVICECODE)))
                        .findFirst()
                        .map(ServiceInfo::getServiceName)
                        .orElse("");
                table.addCell(new PdfPCell(new Phrase(String.valueOf(i + 1), fontBody)));
                table.addCell(new PdfPCell(new Phrase(obj.getString(FileStoreGovTerm.COMBINED_CODE), fontBody)));
                table.addCell(new PdfPCell(new Phrase(obj.getString(FileStoreGovTerm.FILENAME), fontBody)));
                table.addCell(new PdfPCell(new Phrase(obj.getString(FileStoreGovTerm.DOSSIER_NO), fontBody)));
                table.addCell(new PdfPCell(new Phrase(domainName, fontBody)));
                table.addCell(new PdfPCell(new Phrase(serviceName, fontBody)));
                table.addCell(new PdfPCell(new Phrase(obj.getString(FileStoreGovTerm.DISPLAYNAME), fontBody)));
                table.addCell(new PdfPCell(new Phrase(obj.getString(FileStoreGovTerm.GOVAGENCYNAME), fontBody)));
            }

            document.add(table);
            document.close();

        } catch (DocumentException | IOException e) {
            _log.error("Error generating PDF", e);
        }
        return Response.ok(baos.toByteArray(), MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"Giấy tờ số hoá.pdf\"")
                .build();

    }


    private void applyStylesAndAutoSize(XSSFWorkbook workbook, Sheet sheet, String[] headers) {
        CellStyle headerStyle = workbook.createCellStyle();
        XSSFFont headerFont = workbook.createFont();
        headerFont.setFontName("Times New Roman");
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        CellStyle dataStyle = workbook.createCellStyle();
        XSSFFont dataFont = workbook.createFont();
        dataFont.setFontName("Times New Roman");
        dataStyle.setFont(dataFont);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);

        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                for (int j = 0; j < headers.length; j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    if (i <= 3) {
                        cell.setCellStyle(headerStyle);
                        if (j == 0) headerStyle.setAlignment(HorizontalAlignment.LEFT);
                    } else {
                        cell.setCellStyle(dataStyle);
                        if (j == 0) dataStyle.setAlignment(HorizontalAlignment.LEFT);
                    }
                }
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }


    private Response exportFile(XSSFWorkbook workbook, User user, Company company, Long groupId, ServiceContext serviceContext)
            throws IOException, JSONException {
        File exportDir = new File(StatisticManagementConstants.FOLDER_EXPORTED);
        if (!exportDir.exists()) exportDir.mkdirs();

        File xlsFile = new File(exportDir, System.currentTimeMillis() + ConstantUtils.DOT_XLSX);
        String fileName = "Thống kê tình hình số hóa hồ sơ" + ConstantUtils.DOT_XLSX;

        try (FileOutputStream outputStream = new FileOutputStream(xlsFile)) {
            workbook.write(outputStream);
            workbook.close();

            JSONObject result = JSONFactoryUtil.createJSONObject();
            try (FileInputStream fis = new FileInputStream(xlsFile)) {
                FileEntry fileEntry = backend.utils.FileUploadUtils.uploadFile(user.getUserId(), company.getCompanyId(), groupId,
                        fis, fileName, ConstantUtils.MEDIA_TYPE_EXCEL, fis.available(), StringPool.BLANK, StringPool.BLANK, serviceContext);
                if (Validator.isNull(fileEntry)) {
                    result.put(ConstantUtils.API_JSON_MESSAGE, "Không thể tạo bản ghi file entry kết quả");
                    result.put(ConstantUtils.API_JSON_STATUS, ConstantUtils.API_JSON_STATUS_CODE_BAD);
                    return Response.ok(result.toJSONString()).build();
                }

                String downloadUrl = String.format("/documents/%d/%d/download/%s?t=%d",
                        fileEntry.getRepositoryId(), fileEntry.getFolderId(), URLCodec.encodeURL(fileEntry.getUuid()), System.currentTimeMillis());
                result.put(ConstantUtils.API_JSON_STATUS, ConstantUtils.API_JSON_STATUS_CODE_OK);
                result.put("downloadUrl", downloadUrl);
                return Response.ok(result.toJSONString()).build();
            }
        } catch (Exception e) {
            _log.error(e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Create file fail").build();
        }
    }

    private CellStyle createUpdatedStyle(CellStyle existingStyle, CellStyle newStyle, XSSFFont timesNewRomanFont, boolean wrapText, boolean centerText) {
        timesNewRomanFont.setFontName("Times New Roman");
        if (existingStyle != null) {
            newStyle.cloneStyleFrom(existingStyle);
        }
        newStyle.setFont(timesNewRomanFont);
        newStyle.setWrapText(wrapText);
        if (centerText) {
            newStyle.setAlignment(HorizontalAlignment.CENTER);
            newStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        }

        // Create border
        newStyle.setBorderTop(BorderStyle.THIN);
        newStyle.setBorderBottom(BorderStyle.THIN);
        newStyle.setBorderLeft(BorderStyle.THIN);
        newStyle.setBorderRight(BorderStyle.THIN);
        return newStyle;
    }

    private JSONArray getAllFileStoreGovWithoutScope(FileStoreGovSearchModel searchModel, Long groupId) {
        JSONArray results = JSONFactoryUtil.createJSONArray();
        List<Object> params = new ArrayList<>();

        StringBuilder baseSql = new StringBuilder(
                "  SELECT\n" +
                        "      f.CODENUMBER,\n" +
                        "      f.CODENOTATION,\n" +
                        "      f.FILESTOREGOVID,\n" +
                        "      f.FILENAME,\n" +
                        "      f.DISPLAYNAME,\n" +
                        "      f.SERVICECODE,\n" +
                        "      di.ITEMNAME,\n" +
                        "      di.ITEMCODE,\n" +
                        "      di.DICTITEMID,\n" +
                        "      di.PARENTITEMID,\n" +
                        "      di.LEVEL_,\n" +
                        "      f.DOSSIERNO\n" +
                        "  FROM FILESTOREGOV f\n" +
                        "  LEFT JOIN (\n" +
                        "      SELECT di.ITEMCODE, di.ITEMNAME, di.DICTCOLLECTIONID, di.DICTITEMID, di.PARENTITEMID, di.LEVEL_, dicm.OLD_CODE\n" +
                        "      FROM OPENCPS_DICTITEM di\n" +
                        "      LEFT JOIN CODEMAPPING dicm ON di.ITEMCODE = dicm.CURRENT_CODE\n" +
                        "  ) di ON f.GOVAGENCYCODE = di.ITEMCODE OR f.GOVAGENCYCODE = di.OLD_CODE\n"
        );
        baseSql.append("WHERE f.GOVAGENCYCODE IS NOT NULL AND f.GROUPID = 272638 AND f.CREATEDATE IS NOT NULL\n" +
                "AND di.DICTCOLLECTIONID IN (SELECT dc.DICTCOLLECTIONID FROM OPENCPS_DICTCOLLECTION dc WHERE dc.COLLECTIONCODE = 'GOVERNMENT_AGENCY')\n");

        // Append search conditions — same as before
        if (notEmpty(searchModel.getIsActive())) {
            baseSql.append(" AND f.ISACTIVE = ?");
            params.add(searchModel.getIsActive());
        } else {
            baseSql.append(" AND f.ISACTIVE = ?");
            params.add(1);
        }

        if (notEmpty(searchModel.getPartType())) {
            baseSql.append(" AND f.PARTTYPE = ?");
            params.add(searchModel.getPartType());
        }

        if (notEmpty(searchModel.getServiceCode())) {
            baseSql.append(" AND f.SERVICECODE = ?");
            params.add(searchModel.getServiceCode());
        }

        if (notEmpty(searchModel.getDepartmentIssue())) {
            baseSql.append(" AND LOWER(f.DEPARTMENTISSUE) LIKE ?");
            params.add("%" + searchModel.getDepartmentIssue().toLowerCase() + "%");
        }

        if (searchModel.getFolderId() != null) {
            baseSql.append(" AND f.FILESTOREGOVID IN (SELECT ff.FILESTOREID FROM OPENCPS_FILEFOLDER ff WHERE ff.FILEFOLDERID = ?)");
            params.add(searchModel.getFolderId());
        }

        if (notEmpty(searchModel.getOwnerNo())) {
            baseSql.append(" AND f.OWNERNO LIKE ?");
            params.add("%" + searchModel.getOwnerNo() + "%");
        }

        if (notEmpty(searchModel.getOwnerName())) {
            baseSql.append(" AND LOWER(f.OWNERNAME) LIKE ?");
            params.add("%" + searchModel.getOwnerName().toLowerCase() + "%");
        }

        if (notEmpty(searchModel.getFileName())) {
            baseSql.append(" AND LOWER(f.FILENAME) LIKE ?");
            params.add("%" + searchModel.getFileName().toLowerCase() + "%");
        }

        if (notEmpty(searchModel.getDisplayName())) {
            baseSql.append(" AND LOWER(f.DISPLAYNAME) LIKE ?");
            params.add("%" + searchModel.getDisplayName().toLowerCase() + "%");
        }

        if (notEmpty(searchModel.getCodeNumber())) {
            baseSql.append(" AND f.CODENUMBER LIKE ?");
            params.add("%" + searchModel.getCodeNumber() + "%");
        }

        if (notEmpty(searchModel.getCodeNotation())) {
            baseSql.append(" AND f.CODENOTATION LIKE ?");
            params.add("%" + searchModel.getCodeNotation() + "%");
        }

        if (notEmpty(searchModel.getFullInfo())) {
            baseSql.append(" AND LOWER(f.FULLINFO) LIKE ?");
            params.add("%" + searchModel.getFullInfo().toLowerCase() + "%");
        }

        if (notEmpty(searchModel.getDossierNo())) {
            baseSql.append(" AND f.DOSSIERNO LIKE ?");
            params.add("%" + searchModel.getDossierNo() + "%");
        }

        if (notEmpty(searchModel.getDossierName())) {
            baseSql.append(" AND LOWER(f.DOSSIERNAME) LIKE ?");
            params.add("%" + searchModel.getDossierName().toLowerCase() + "%");
        }

        if (notEmpty(searchModel.getAbstractss())) {
            baseSql.append(" AND LOWER(f.ABSTRACTSS) LIKE ?");
            params.add("%" + searchModel.getAbstractss().toLowerCase() + "%");
        }

        if (notEmpty(searchModel.getCombinedCode())) {
            baseSql.append(" AND (f.CODENUMBER || f.CODENOTATION) LIKE ?");
            params.add("%" + searchModel.getCombinedCode() + "%");
        }

        if (notEmpty(searchModel.getKeyword())) {
            String kw = "%" + searchModel.getKeyword().toLowerCase() + "%";
            baseSql.append(" AND (LOWER(f.FILENAME) LIKE ? OR LOWER(f.DISPLAYNAME) LIKE ? OR LOWER(f.DOSSIERNO) LIKE ? OR LOWER(f.DOSSIERNAME) LIKE ? OR LOWER(f.SERVICECODE) LIKE ?)");
            params.add(kw);
            params.add(kw);
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        if (notEmpty(searchModel.getFromDate()) && notEmpty(searchModel.getToDate())) {
            baseSql.append(" AND f.CREATEDATE BETWEEN TO_DATE(?, 'DD/MM/YYYY') AND TO_DATE(?, 'DD/MM/YYYY')");
            params.add(searchModel.getFromDate());
            params.add(searchModel.getToDate());
        }

        if (notEmpty(searchModel.getIssueDateFrom()) && notEmpty(searchModel.getIssueDateTo())) {
            baseSql.append(" AND f.ISSUEDATE BETWEEN TO_DATE(?, 'DD/MM/YYYY') AND TO_DATE(?, 'DD/MM/YYYY')");
            params.add(searchModel.getIssueDateFrom());
            params.add(searchModel.getIssueDateTo());
        }

        if (notEmpty(searchModel.getDomainCode()) && searchModel.getServiceCode().isEmpty()) {
            List<ServiceInfo> serviceInfoList = ServiceInfoLocalServiceUtil.getServiceInfosByGroupId(MCDT_GROUP_ID, -1, -1);
            List<ServiceInfo> serviceInfoListByDomain = serviceInfoList.stream().filter(service -> service.getDomainCode().equalsIgnoreCase(searchModel.getDomainCode())).collect(Collectors.toList());


            if (!serviceInfoListByDomain.isEmpty()) {
                String serviceCodes = serviceInfoListByDomain.stream()
                        .map(service -> "'" + service.getServiceCode() + "'")
                        .collect(Collectors.joining(","));

                baseSql.append(" AND f.SERVICECODE IN (").append(serviceCodes).append(")\n");
            }
        }

        if (!searchModel.getGovAgencyCode().isEmpty()) {
            Boolean isSiblingSearch = searchModel.getSiblingSearch();
            if (Validator.isNull(isSiblingSearch) || !isSiblingSearch) {
                baseSql.append(" AND f.GOVAGENCYCODE = ? ");
                params.add(searchModel.getGovAgencyCode());
            } else {
                DictCollection dictCollection = DictCollectionLocalServiceUtil.fetchByF_dictCollectionCode(GOVERNMENT_AGENCY, groupId);
                if (Validator.isNotNull(dictCollection)) {
                    DictItem dictItem = DictItemLocalServiceUtil.fetchByF_dictItemCode(searchModel.getGovAgencyCode(), dictCollection.getDictCollectionId(), groupId);
                    String scopeList = null;
                    if (Validator.isNotNull(dictItem)) {
                        scopeList = ScopeUtils.getAllSubUnit(dictItem, dictCollection.getDictCollectionId(), groupId);
                    }
                    if (scopeList != null) {
                        List<String> scopeItems = Arrays.asList(scopeList.split(StringPool.COMMA));
                        String placeholders = scopeItems.stream().map(item -> "?").collect(Collectors.joining(","));
                        baseSql.append(" AND f.GOVAGENCYCODE IN (").append(placeholders).append(") ");
                        for (String code : scopeItems) {
                            params.add(code.trim());
                        }
                    } else {
                        baseSql.append(" AND f.GOVAGENCYCODE = ? ");
                        params.add(searchModel.getGovAgencyCode());
                    }
                }
            }
        }

        queryAndAppendFileStoreGov(baseSql.toString(), results, params);
        return results;
    }

    private JSONArray getAllFileStoreGovWithScope(String scope, FileStoreGovSearchModel searchModel, Long groupId) {
        final int BATCH_SIZE = 500;
        JSONArray results = JSONFactoryUtil.createJSONArray();
        List<Object> params = new ArrayList<>();

        StringBuilder baseSql = new StringBuilder(
                "  SELECT\n" +
                        "      f.CODENUMBER,\n" +
                        "      f.CODENOTATION,\n" +
                        "      f.FILESTOREGOVID,\n" +
                        "      f.FILENAME,\n" +
                        "      f.DISPLAYNAME,\n" +
                        "      f.SERVICECODE,\n" +
                        "      di.ITEMNAME,\n" +
                        "      di.ITEMCODE,\n" +
                        "      di.DICTITEMID,\n" +
                        "      di.PARENTITEMID,\n" +
                        "      di.LEVEL_,\n" +
                        "      f.DOSSIERNO\n" +
                        "  FROM FILESTOREGOV f\n" +
                        "  LEFT JOIN (\n" +
                        "      SELECT di.ITEMCODE, di.ITEMNAME, di.DICTCOLLECTIONID, di.DICTITEMID, di.PARENTITEMID, di.LEVEL_, dicm.OLD_CODE\n" +
                        "      FROM OPENCPS_DICTITEM di\n" +
                        "      LEFT JOIN CODEMAPPING dicm ON di.ITEMCODE = dicm.CURRENT_CODE\n" +
                        "  ) di ON f.GOVAGENCYCODE = di.ITEMCODE OR f.GOVAGENCYCODE = di.OLD_CODE\n"
        );
        baseSql.append("WHERE f.GOVAGENCYCODE IS NOT NULL AND f.GROUPID = 272638 AND f.CREATEDATE IS NOT NULL\n" +
                "AND di.DICTCOLLECTIONID IN (SELECT dc.DICTCOLLECTIONID FROM OPENCPS_DICTCOLLECTION dc WHERE dc.COLLECTIONCODE = 'GOVERNMENT_AGENCY')\n");

        // All your filter logic below remains unchanged
        if (notEmpty(searchModel.getIsActive())) {
            baseSql.append(" AND f.ISACTIVE = ?");
            params.add(searchModel.getIsActive());
        } else {
            baseSql.append(" AND f.ISACTIVE = ?");
            params.add(1);
        }

        if (notEmpty(searchModel.getPartType())) {
            baseSql.append(" AND f.PARTTYPE = ?");
            params.add(searchModel.getPartType());
        }

        if (notEmpty(searchModel.getServiceCode())) {
            baseSql.append(" AND f.SERVICECODE = ?");
            params.add(searchModel.getServiceCode());
        }

        if (notEmpty(searchModel.getDepartmentIssue())) {
            baseSql.append(" AND LOWER(f.DEPARTMENTISSUE) LIKE ?");
            params.add("%" + searchModel.getDepartmentIssue().toLowerCase() + "%");
        }

        if (searchModel.getFolderId() != null) {
            baseSql.append(" AND f.FILESTOREGOVID IN (SELECT ff.FILESTOREID FROM OPENCPS_FILEFOLDER ff WHERE ff.FILEFOLDERID = ?)");
            params.add(searchModel.getFolderId());
        }

        if (notEmpty(searchModel.getOwnerNo())) {
            baseSql.append(" AND f.OWNERNO LIKE ?");
            params.add("%" + searchModel.getOwnerNo() + "%");
        }

        if (notEmpty(searchModel.getOwnerName())) {
            baseSql.append(" AND LOWER(f.OWNERNAME) LIKE ?");
            params.add("%" + searchModel.getOwnerName().toLowerCase() + "%");
        }

        if (notEmpty(searchModel.getFileName())) {
            baseSql.append(" AND LOWER(f.FILENAME) LIKE ?");
            params.add("%" + searchModel.getFileName().toLowerCase() + "%");
        }

        if (notEmpty(searchModel.getDisplayName())) {
            baseSql.append(" AND LOWER(f.DISPLAYNAME) LIKE ?");
            params.add("%" + searchModel.getDisplayName().toLowerCase() + "%");
        }

        if (notEmpty(searchModel.getCodeNumber())) {
            baseSql.append(" AND f.CODENUMBER LIKE ?");
            params.add("%" + searchModel.getCodeNumber() + "%");
        }

        if (notEmpty(searchModel.getCodeNotation())) {
            baseSql.append(" AND f.CODENOTATION LIKE ?");
            params.add("%" + searchModel.getCodeNotation() + "%");
        }

        if (notEmpty(searchModel.getFullInfo())) {
            baseSql.append(" AND LOWER(f.FULLINFO) LIKE ?");
            params.add("%" + searchModel.getFullInfo().toLowerCase() + "%");
        }

        if (notEmpty(searchModel.getDossierNo())) {
            baseSql.append(" AND f.DOSSIERNO LIKE ?");
            params.add("%" + searchModel.getDossierNo() + "%");
        }

        if (notEmpty(searchModel.getDossierName())) {
            baseSql.append(" AND LOWER(f.DOSSIERNAME) LIKE ?");
            params.add("%" + searchModel.getDossierName().toLowerCase() + "%");
        }

        if (notEmpty(searchModel.getAbstractss())) {
            baseSql.append(" AND LOWER(f.ABSTRACTSS) LIKE ?");
            params.add("%" + searchModel.getAbstractss().toLowerCase() + "%");
        }

        if (notEmpty(searchModel.getCombinedCode())) {
            baseSql.append(" AND (f.CODENUMBER || f.CODENOTATION) LIKE ?");
            params.add("%" + searchModel.getCombinedCode() + "%");
        }

        if (notEmpty(searchModel.getKeyword())) {
            String kw = "%" + searchModel.getKeyword().toLowerCase() + "%";
            baseSql.append(" AND (LOWER(f.FILENAME) LIKE ? OR LOWER(f.DISPLAYNAME) LIKE ? OR LOWER(f.DOSSIERNO) LIKE ? OR LOWER(f.DOSSIERNAME) LIKE ? OR LOWER(f.SERVICECODE) LIKE ?)");
            params.add(kw);
            params.add(kw);
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        if (notEmpty(searchModel.getFromDate()) && notEmpty(searchModel.getToDate())) {
            baseSql.append(" AND f.CREATEDATE BETWEEN TO_DATE(?, 'DD/MM/YYYY') AND TO_DATE(?, 'DD/MM/YYYY')");
            params.add(searchModel.getFromDate());
            params.add(searchModel.getToDate());
        }

        if (notEmpty(searchModel.getIssueDateFrom()) && notEmpty(searchModel.getIssueDateTo())) {
            baseSql.append(" AND f.ISSUEDATE BETWEEN TO_DATE(?, 'DD/MM/YYYY') AND TO_DATE(?, 'DD/MM/YYYY')");
            params.add(searchModel.getIssueDateFrom());
            params.add(searchModel.getIssueDateTo());
        }

        if (notEmpty(searchModel.getDomainCode()) && searchModel.getServiceCode().isEmpty()) {
            List<ServiceInfo> serviceInfoList = ServiceInfoLocalServiceUtil.getServiceInfosByGroupId(MCDT_GROUP_ID, -1, -1);
            List<ServiceInfo> serviceInfoListByDomain = serviceInfoList.stream().filter(service -> service.getDomainCode().equalsIgnoreCase(searchModel.getDomainCode())).collect(Collectors.toList());
            if (!serviceInfoListByDomain.isEmpty()) {
                String serviceCodes = serviceInfoListByDomain.stream()
                        .map(service -> "'" + service.getServiceCode() + "'")
                        .collect(Collectors.joining(","));
                baseSql.append(" AND f.SERVICECODE IN (").append(serviceCodes).append(")\n");
            }
        }

        if (scope != null && searchModel.getGovAgencyCode().isEmpty()) {
            List<String> govCodes = Arrays.stream(scope.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty()).distinct()
                    .collect(Collectors.toList());
            for (int i = 0; i < govCodes.size(); i += BATCH_SIZE) {
                int end = Math.min(i + BATCH_SIZE, govCodes.size());
                List<String> batch = govCodes.subList(i, end);
                String inClause = String.join(",", batch);

                String inList = batch.stream().collect(Collectors.joining(", "));

                StringBuilder sql = new StringBuilder(baseSql.toString());
                sql.append(" AND f.GOVAGENCYCODE IN (").append(inList).append(") ")
                        .append(" AND di.DICTCOLLECTIONID IN (SELECT dc.DICTCOLLECTIONID FROM OPENCPS_DICTCOLLECTION dc WHERE dc.COLLECTIONCODE = 'GOVERNMENT_AGENCY')\n");
                queryAndAppendFileStoreGov(sql.toString(), results, params);
            }
        }
        // Specific GOVAGENCYCODE provided
        else if (!searchModel.getGovAgencyCode().isEmpty()) {
            Boolean isSiblingSearch = searchModel.getSiblingSearch();
            List<String> scopeItems = new ArrayList<>();

            if (Validator.isNull(isSiblingSearch) || !isSiblingSearch) {
                scopeItems = Collections.singletonList(searchModel.getGovAgencyCode());
            } else {
                DictCollection dictCollection = DictCollectionLocalServiceUtil.fetchByF_dictCollectionCode(GOVERNMENT_AGENCY, groupId);
                if (Validator.isNotNull(dictCollection)) {
                    DictItem dictItem = DictItemLocalServiceUtil.fetchByF_dictItemCode(searchModel.getGovAgencyCode(), dictCollection.getDictCollectionId(), groupId);
                    if (Validator.isNotNull(dictItem)) {
                        String scopeList = ScopeUtils.getAllSubUnit(dictItem, dictCollection.getDictCollectionId(), groupId);
                        scopeItems = Arrays.asList(scopeList.split(StringPool.COMMA));
                    } else {
                        scopeItems = Collections.singletonList(searchModel.getGovAgencyCode());
                    }
                }
            }

            String placeholders = scopeItems.stream().map(code -> "?").collect(Collectors.joining(","));
            baseSql.append(" AND f.GOVAGENCYCODE IN (").append(placeholders).append(")");

            for (String code : scopeItems) {
                params.add(code.trim());
            }


            queryAndAppendFileStoreGov(baseSql.toString(), results, params);
        } else {
            // No scope or GOVAGENCYCODE filtering
            queryAndAppendFileStoreGov(baseSql.toString(), results, params);
        }

        return results;
    }


    private JSONArray getAllFileStoreGov(String scope) {
        final int BATCH_SIZE = 500;
        JSONArray results = JSONFactoryUtil.createJSONArray();

        if (scope != null) {
            // Tách danh sách GOVAGENCYCODE
            List<String> govCodes = Arrays.stream(scope.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty()).distinct()
                    .collect(Collectors.toList());

            // Chia nhỏ từng batch và query
            for (int i = 0; i < govCodes.size(); i += BATCH_SIZE) {
                int end = Math.min(i + BATCH_SIZE, govCodes.size());
                List<String> batch = govCodes.subList(i, end);
                String inClause = String.join(",", batch);

                String sql = "SELECT\n" +
                        "    f.CODENUMBER,\n" +
                        "    f.CODENOTATION,\n" +
                        "    f.FILESTOREGOVID,\n" +
                        "    f.FILENAME,\n" +
                        "    f.DISPLAYNAME,\n" +
                        "    di.ITEMNAME,\n" +
                        "    di.ITEMCODE,\n" +
                        "    di.DICTITEMID,\n" +
                        "    di.PARENTITEMID,\n" +
                        "    di.LEVEL_,\n" +
                        "    f.DOSSIERNO\n" +
                        "FROM FILESTOREGOV f\n" +
                        "LEFT JOIN (\n" +
                        "    SELECT\n" +
                        "        di.ITEMCODE,\n" +
                        "        di.ITEMNAME,\n" +
                        "        di.DICTCOLLECTIONID,\n" +
                        "        di.DICTITEMID,\n" +
                        "        di.PARENTITEMID,\n" +
                        "        di.LEVEL_,\n" +
                        "        dicm.OLD_CODE\n" +
                        "    FROM OPENCPS_DICTITEM di\n" +
                        "    LEFT JOIN CODEMAPPING dicm\n" +
                        "           ON di.ITEMCODE = dicm.CURRENT_CODE\n" +
                        ") di\n" +
                        "       ON f.GOVAGENCYCODE = di.ITEMCODE\n" +
                        "       OR f.GOVAGENCYCODE = di.OLD_CODE\n" +
                        "WHERE f.ISACTIVE = 1\n" +
                        "  AND f.GOVAGENCYCODE IS NOT NULL\n" +
                        "  AND f.CREATEDATE IS NOT NULL\n" +
                        "  AND f.GOVAGENCYCODE IN (" + inClause + ")\n" +
                        "  AND di.DICTCOLLECTIONID IN (\n" +
                        "        SELECT dc.DICTCOLLECTIONID\n" +
                        "        FROM OPENCPS_DICTCOLLECTION dc\n" +
                        "        WHERE dc.COLLECTIONCODE = 'GOVERNMENT_AGENCY'\n" +
                        "  )\n" +
                        "ORDER BY di.ITEMNAME DESC";

                queryAndAppendFileStoreGov(sql, results);
            }
        } else {
            // Không có scope: truy tất cả
            String sql = "SELECT\n" +
                    "    f.CODENUMBER,\n" +
                    "    f.CODENOTATION,\n" +
                    "    f.FILESTOREGOVID,\n" +
                    "    f.FILENAME,\n" +
                    "    f.DISPLAYNAME,\n" +
                    "    di.ITEMNAME,\n" +
                    "    di.ITEMCODE,\n" +
                    "    di.DICTITEMID,\n" +
                    "    di.PARENTITEMID,\n" +
                    "    di.LEVEL_,\n" +
                    "    f.DOSSIERNO\n" +
                    "FROM FILESTOREGOV f\n" +
                    "LEFT JOIN (\n" +
                    "    SELECT\n" +
                    "        di.ITEMCODE,\n" +
                    "        di.ITEMNAME,\n" +
                    "        di.DICTCOLLECTIONID,\n" +
                    "        di.DICTITEMID,\n" +
                    "        di.PARENTITEMID,\n" +
                    "        di.LEVEL_,\n" +
                    "        dicm.OLD_CODE\n" +
                    "    FROM OPENCPS_DICTITEM di\n" +
                    "    LEFT JOIN CODEMAPPING dicm\n" +
                    "           ON di.ITEMCODE = dicm.CURRENT_CODE\n" +
                    ") di\n" +
                    "       ON f.GOVAGENCYCODE = di.ITEMCODE\n" +
                    "       OR f.GOVAGENCYCODE = di.OLD_CODE\n" +
                    "WHERE f.ISACTIVE = 1\n" +
                    "  AND f.GOVAGENCYCODE IS NOT NULL\n" +
                    "  AND f.CREATEDATE IS NOT NULL\n" +
                    "  AND di.DICTCOLLECTIONID IN (\n" +
                    "        SELECT dc.DICTCOLLECTIONID\n" +
                    "        FROM OPENCPS_DICTCOLLECTION dc\n" +
                    "        WHERE dc.COLLECTIONCODE = 'GOVERNMENT_AGENCY'\n" +
                    "  )\n" +
                    "ORDER BY di.ITEMNAME DESC";

            queryAndAppendFileStoreGov(sql, results);
        }

        return results;
    }


    private void queryAndAppendFileStoreGov(String sql, JSONArray results) {
        DataSource dataSource = InfrastructureUtil.getDataSource();
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement pst = connection.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()
        ) {
            while (rs.next()) {
                JSONObject result = JSONFactoryUtil.createJSONObject();
                result.put(FileStoreGovTerm.CODENUMBER, rs.getString("CODENUMBER"));
                result.put(FileStoreGovTerm.CODENOTATION, rs.getString("CODENOTATION"));
                result.put(FileStoreGovTerm.FILENAME, rs.getString("FILENAME"));
                result.put(FileStoreGovTerm.DISPLAYNAME, rs.getString("DISPLAYNAME"));
                result.put(FileStoreGovTerm.GOVAGENCYNAME, rs.getString("ITEMNAME"));
                result.put(FileStoreGovTerm.GOVAGENCYCODE, rs.getString("ITEMCODE"));
                result.put(FileStoreGovTerm.DOSSIER_NO, rs.getString("DOSSIERNO"));
                result.put(DictItemTerm.DICT_ITEM_ID, rs.getString("DICTITEMID"));
                result.put(DictItemTerm.PARENT_ITEM_ID, rs.getString("PARENTITEMID"));
                result.put(DictItemTerm.LEVEL, rs.getString("LEVEL_"));
                result.put(FileStoreGovTerm.FILESTOREGOVID, rs.getString("FILESTOREGOVID"));
                results.put(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void queryAndAppendFileStoreGovDomain(String sql, JSONArray results) {
        DataSource dataSource = InfrastructureUtil.getDataSource();
        // Dùng HashSet để lưu các domain code đã có trong results
        Set<String> existingDomainCodes = new HashSet<>();

        // Nạp sẵn các domain code có sẵn trong results (nếu đã có từ lần trước)
        for (int i = 0; i < results.length(); i++) {
            JSONObject obj = results.getJSONObject(i);
            existingDomainCodes.add(obj.getString(FileStoreGovTerm.DOMAIN_CODE));
        }

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement pst = connection.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()
        ) {
            while (rs.next()) {
                String domainCode = rs.getString("ITEMCODE");
                String domainName = rs.getString("ITEMNAME");

                // Chỉ thêm nếu chưa có domainCode trong danh sách
                if (!existingDomainCodes.contains(domainCode)) {
                    JSONObject result = JSONFactoryUtil.createJSONObject();
                    result.put(FileStoreGovTerm.DOMAIN_CODE, domainCode);
                    result.put(FileStoreGovTerm.DOMAIN_NAME, domainName);
                    results.put(result);

                    existingDomainCodes.add(domainCode); // thêm vào set để lần sau khỏi trùng
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void queryAndAppendFileStoreGov(String sql, JSONArray results, List<Object> params) {
        DataSource dataSource = InfrastructureUtil.getDataSource();
        Set<String> seenFileStoreGovIds = new HashSet<>(); // lưu lại FILESTOREGOVID đã gặp
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement pst = connection.prepareStatement(sql)
        ) {
            // Set parameters in PreparedStatement
            if (params != null) {
                for (int i = 0; i < params.size(); i++) {
                    pst.setObject(i + 1, params.get(i));  // JDBC parameters are 1-based
                }
            }

            List<ServiceInfo> serviceInfoList = ServiceInfoLocalServiceUtil.getServiceInfosByGroupId(MCDT_GROUP_ID, -1, -1);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String fileStoreGovId = rs.getString("FILESTOREGOVID");
                    if (seenFileStoreGovIds.contains(fileStoreGovId)) {
                        continue;
                    }

                    String fileDossierServiceCode = rs.getString("SERVICECODE");

                    ServiceInfo serviceInfo = serviceInfoList.stream()
                            .filter(service -> service.getServiceCode().equals(fileDossierServiceCode))
                            .findFirst()
                            .orElse(null);

                    JSONObject result = JSONFactoryUtil.createJSONObject();
                    result.put(FileStoreGovTerm.CODENUMBER, rs.getString("CODENUMBER"));
                    result.put(FileStoreGovTerm.CODENOTATION, rs.getString("CODENOTATION"));
                    result.put(FileStoreGovTerm.FILENAME, rs.getString("FILENAME"));
                    result.put(FileStoreGovTerm.DISPLAYNAME, rs.getString("DISPLAYNAME"));
                    result.put(FileStoreGovTerm.GOVAGENCYNAME, rs.getString("ITEMNAME"));
                    result.put(FileStoreGovTerm.GOVAGENCYCODE, rs.getString("ITEMCODE"));
                    result.put(FileStoreGovTerm.DOSSIER_NO, rs.getString("DOSSIERNO"));
                    if (!Objects.isNull(serviceInfo)) {
                        result.put(FileStoreGovTerm.SERVICE_NAME, serviceInfo.getServiceName());
                        result.put(FileStoreGovTerm.DOMAIN_NAME, serviceInfo.getDomainName());
                    } else {
                        result.put(FileStoreGovTerm.SERVICE_NAME, "");
                        result.put(FileStoreGovTerm.DOMAIN_NAME, "");
                    }
                    result.put(DictItemTerm.DICT_ITEM_ID, rs.getString("DICTITEMID"));
                    result.put(DictItemTerm.PARENT_ITEM_ID, rs.getString("PARENTITEMID"));
                    result.put(DictItemTerm.LEVEL, rs.getString("LEVEL_"));
                    result.put(FileStoreGovTerm.FILESTOREGOVID, rs.getString("FILESTOREGOVID"));
                    results.put(result);
                    seenFileStoreGovIds.add(fileStoreGovId); // đánh dấu đã gặp
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void queryAndAppendDossierNoCount(String sql, Map<String, Integer> resultMap, List<Object> params) {
        DataSource dataSource = InfrastructureUtil.getDataSource();
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement pst = connection.prepareStatement(sql)
        ) {
            if (params != null) {
                for (int i = 0; i < params.size(); i++) {
                    pst.setObject(i + 1, params.get(i)); // chỉ số bắt đầu từ 1
                }
            }
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int dossierNoCount = rs.getInt("TOTAL_DOSSIERNO");

                    // Lấy giá trị cũ (nếu có) trong map
                    int oldValue = resultMap.getOrDefault("dossierNoCount", 0);

                    // Cộng dồn thay vì ghi đè
                    resultMap.put("dossierNoCount", oldValue + dossierNoCount);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void queryAndAppendUsageCount(String sql, Map<String, Integer> resultMap, List<Object> params) {
        DataSource dataSource = InfrastructureUtil.getDataSource();

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement pst = connection.prepareStatement(sql)
        ) {
            if (params != null) {
                for (int i = 0; i < params.size(); i++) {
                    pst.setObject(i + 1, params.get(i)); // chỉ số bắt đầu từ 1
                }
            }
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int usageCount = rs.getInt("USAGE_COUNT");

                    // Lấy giá trị cũ từ map (nếu có)
                    int oldValue = resultMap.getOrDefault("usageCount", 0);

                    // Cộng dồn vào map
                    resultMap.put("usageCount", oldValue + usageCount);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private JSONArray getAllFileStoreGovBk(String scope) {
        String sql = "SELECT f.CODENUMBER, f.CODENOTATION, f.FILENAME, f.DISPLAYNAME, di.ITEMNAME\n" +
                "FROM FILESTOREGOV f\n" +
                "         left join OPENCPS_DICTITEM di on f.GOVAGENCYCODE = di.ITEMCODE\n" +
                "WHERE f.ISACTIVE = 1\n" +
                "  and f.GOVAGENCYCODE is not null\n" +
                "  and di.DICTCOLLECTIONID in\n" +
                "      (select DICTCOLLECTIONID from OPENCPS_DICTCOLLECTION where COLLECTIONCODE = 'GOVERNMENT_AGENCY')\n" +
                "order by di.ITEMNAME desc";
        if (scope != null) {
            sql = "SELECT f.CODENUMBER, f.CODENOTATION, f.FILENAME, f.DISPLAYNAME, di.ITEMNAME\n" +
                    "FROM FILESTOREGOV f\n" +
                    "         left join OPENCPS_DICTITEM di on f.GOVAGENCYCODE = di.ITEMCODE\n" +
                    "WHERE f.ISACTIVE = 1\n" +
                    "  and f.GOVAGENCYCODE is not null\n" +
                    "  AND f.GOVAGENCYCODE in (" + scope + ") " +
                    "  and di.DICTCOLLECTIONID in\n" +
                    "      (select DICTCOLLECTIONID from OPENCPS_DICTCOLLECTION where COLLECTIONCODE = 'GOVERNMENT_AGENCY')\n" +
                    "order by di.ITEMNAME desc";
        }
        DataSource dataSource = InfrastructureUtil.getDataSource();
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement pst = connection.prepareStatement(sql)
        ) {
            try (ResultSet rs = pst.executeQuery()) {

                JSONArray results = JSONFactoryUtil.createJSONArray();

                while (rs.next()) {

                    JSONObject result = JSONFactoryUtil.createJSONObject();

                    result.put("codenumber", rs.getString("CODENUMBER"));
                    result.put("codenotation", rs.getString("CODENOTATION"));
                    result.put("fileName", rs.getString("FILENAME"));
                    result.put("displayName", rs.getString("DISPLAYNAME"));
                    result.put("govAgencyName", rs.getString("ITEMNAME"));
                    results.put(result);
                }
                return results;
            }
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private Map<String, JSONObject> statisticGovAgencyCode(String subGovs) {
        final int BATCH_SIZE = 500;
        Map<String, JSONObject> statisticGovAgencyCodeMap = new HashMap<>();

        // Nếu subGovs không null thì tách và xử lý theo batch
        if (subGovs != null) {
            // Tách chuỗi thành danh sách từng GOVAGENCYCODE
            List<String> govCodes = Arrays.stream(subGovs.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty()).distinct()
                    .collect(Collectors.toList());

            // Chia danh sách thành các batch nhỏ (500 mỗi lần)
            for (int i = 0; i < govCodes.size(); i += BATCH_SIZE) {
                int end = Math.min(i + BATCH_SIZE, govCodes.size());
                List<String> batch = govCodes.subList(i, end);
                String inClause = batch.stream()
                        .collect(Collectors.joining(","));

                String sql = "SELECT f.GOVAGENCYCODE AS GOVAGENCYCODE,\n" +
                        "       COUNT(DISTINCT f.FILESTOREGOVID) AS SOGIAYTOSOHOA,\n" +
                        "       COUNT(DISTINCT f.DOSSIERNO) AS SOLUONGHOSODASOHOA,\n" +
                        "       COUNT(CASE WHEN fuh.ACTIONSS = 'Tái sử dụng' THEN fuh.FILESTOREGOVID END) AS SOLANSUDUNG\n" +
                        "FROM FILESTOREGOV f\n" +
                        "LEFT JOIN FILEGOVUSEDHISTORY fuh ON f.FILESTOREGOVID = fuh.FILESTOREGOVID\n" +
                        "WHERE f.ISACTIVE = 1 \n" +
                        "  AND f.GOVAGENCYCODE IS NOT NULL\n" +
                        "  AND f.CREATEDATE is not null  \n" +
                        "  AND f.GOVAGENCYCODE IN (" + inClause + ")\n" +
                        "GROUP BY f.GOVAGENCYCODE";

                queryAndMergeResults(sql, statisticGovAgencyCodeMap);
            }
        } else {
            // Nếu không có subGovs, truy vấn toàn bộ
            String sql = "SELECT f.GOVAGENCYCODE AS GOVAGENCYCODE,\n" +
                    "       COUNT(DISTINCT f.FILESTOREGOVID) AS SOGIAYTOSOHOA,\n" +
                    "       COUNT(DISTINCT f.DOSSIERNO) AS SOLUONGHOSODASOHOA,\n" +
                    "       COUNT(CASE WHEN fuh.ACTIONSS = 'Tái sử dụng' THEN fuh.FILESTOREGOVID END) AS SOLANSUDUNG\n" +
                    "FROM FILESTOREGOV f\n" +
                    "LEFT JOIN FILEGOVUSEDHISTORY fuh ON f.FILESTOREGOVID = fuh.FILESTOREGOVID\n" +
                    "WHERE f.ISACTIVE = 1 \n" +
                    "  AND f.GOVAGENCYCODE IS NOT NULL \n" +
                    "  AND f.CREATEDATE is not null  \n" +
                    "GROUP BY f.GOVAGENCYCODE";

            queryAndMergeResults(sql, statisticGovAgencyCodeMap);
        }

        return statisticGovAgencyCodeMap;
    }

    private void queryAndMergeResults(String sql, Map<String, JSONObject> resultMap) {
        DataSource dataSource = InfrastructureUtil.getDataSource();
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement pst = connection.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()
        ) {
            while (rs.next()) {
                String govAgencyCode = rs.getString("GOVAGENCYCODE");

                long total = rs.getLong("SOGIAYTOSOHOA");
                long totalUsage = rs.getLong("SOLANSUDUNG");
                long dossierNoCount = rs.getLong("SOLUONGHOSODASOHOA");

                JSONObject result = resultMap.getOrDefault(govAgencyCode, JSONFactoryUtil.createJSONObject());
                result.put("govAgencyCode", govAgencyCode);
                result.put("total", result.getLong("total", 0) + total);
                result.put("totalUsage", result.getLong("totalUsage", 0) + totalUsage);
                result.put("dossierNoCount", result.getLong("dossierNoCount", 0) + dossierNoCount);

                resultMap.put(govAgencyCode, result);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    private Map<String, JSONObject> statisticGovAgencyCodeBk(String subGovs) {
        String sql = "SELECT f.GOVAGENCYCODE                                                         AS GOVAGENCYCODE,\n" +
                "       COUNT(DISTINCT f.FILESTOREGOVID)                                             AS SOGIAYTOSOHOA,\n" +
                "       COUNT(CASE WHEN fuh.ACTIONSS = 'Tái sử dụng' THEN fuh.FILESTOREGOVID END) AS SOLANSUDUNG\n" +
                "FROM FILESTOREGOV f\n" +
                "         LEFT JOIN FILEGOVUSEDHISTORY fuh ON f.FILESTOREGOVID = fuh.FILESTOREGOVID\n" +
                "WHERE f.ISACTIVE = 1 \n" +
                "  AND f.GOVAGENCYCODE IS NOT NULL\n" +
                "GROUP BY f.GOVAGENCYCODE";
        if (subGovs != null) {
            sql = "SELECT f.GOVAGENCYCODE                                                         AS GOVAGENCYCODE,\n" +
                    "       COUNT(DISTINCT f.FILESTOREGOVID)                                             AS SOGIAYTOSOHOA,\n" +
                    "       COUNT(CASE WHEN fuh.ACTIONSS = 'Tái sử dụng' THEN fuh.FILESTOREGOVID END) AS SOLANSUDUNG\n" +
                    "FROM FILESTOREGOV f\n" +
                    "         LEFT JOIN FILEGOVUSEDHISTORY fuh ON f.FILESTOREGOVID = fuh.FILESTOREGOVID\n" +
                    "WHERE f.ISACTIVE = 1 \n" +
                    "  AND f.GOVAGENCYCODE IS NOT NULL\n" +
                    "  AND f.GOVAGENCYCODE in (" + subGovs + ") " +
                    "GROUP BY f.GOVAGENCYCODE";
        }
        DataSource dataSource = InfrastructureUtil.getDataSource();
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement pst = connection.prepareStatement(sql)
        ) {
            try (ResultSet rs = pst.executeQuery()) {
                Map<String, JSONObject> statisticGovAgencyCodeMap = new HashMap<>();

                while (rs.next()) {
                    String govAgencyCode = rs.getString("GOVAGENCYCODE");
                    JSONObject result = JSONFactoryUtil.createJSONObject();
                    result.put("govAgencyCode", rs.getString("GOVAGENCYCODE"));
                    result.put("total", rs.getLong("SOGIAYTOSOHOA"));
                    result.put("totalUsage", rs.getLong("SOLANSUDUNG"));
                    statisticGovAgencyCodeMap.put(govAgencyCode, result);
                }
                return statisticGovAgencyCodeMap;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private List<DictItem> statisticDictItem(String scope) {
        List<DictItem> allItems = new ArrayList<>();

        // Nếu không có scope → chạy truy vấn mặc định
        if (scope == null) {
            String sql = "SELECT distinct DICTITEMID, ITEMCODE, ITEMNAME, PARENTITEMID, LEVEL_\n" +
                    "FROM OPENCPS_DICTITEM\n" +
                    "START WITH ITEMCODE IN (\n" +
                    "    SELECT GOVAGENCYCODE\n" +
                    "    FROM FILESTOREGOV f\n" +
                    "    WHERE f.ISACTIVE = 1 \n" +
                    "   AND f.CREATEDATE is not null  \n" +
                    "      AND f.GOVAGENCYCODE IS NOT NULL\n" +
                    ")\n" +
                    "AND DICTCOLLECTIONID IN (\n" +
                    "    SELECT DICTCOLLECTIONID\n" +
                    "    FROM OPENCPS_DICTCOLLECTION\n" +
                    "    WHERE COLLECTIONCODE = 'GOVERNMENT_AGENCY'\n" +
                    ")\n" +
                    "CONNECT BY PRIOR PARENTITEMID = DICTITEMID\n" +
                    "ORDER BY LEVEL_, ITEMNAME";
            allItems.addAll(executeQueryForDictItem(sql));
        } else {
            // Tách scope → xử lý batch 500 phần tử
            String[] scopeArray = scope.split(",");
            List<String> scopeList = Arrays.stream(scopeArray)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty()).distinct()
                    .collect(Collectors.toList());

            int batchSize = 500;
            for (int i = 0; i < scopeList.size(); i += batchSize) {
                List<String> batch = scopeList.subList(i, Math.min(i + batchSize, scopeList.size()));
                String inClause = String.join(",", batch);

                String sql = "SELECT DISTINCT DICTITEMID, ITEMCODE, ITEMNAME, PARENTITEMID, LEVEL_\n" +
                        "FROM OPENCPS_DICTITEM\n" +
                        "WHERE ITEMCODE IN (\n" +
                        "    SELECT GOVAGENCYCODE\n" +
                        "    FROM FILESTOREGOV f\n" +
                        "    WHERE f.ISACTIVE = 1\n" +
                        "      AND f.GOVAGENCYCODE IS NOT NULL \n" +
                        "   AND f.CREATEDATE is not null  \n" +
                        "      AND f.GOVAGENCYCODE IN (" + inClause + ")\n" +
                        ")\n" +
                        "AND DICTCOLLECTIONID IN (\n" +
                        "    SELECT DICTCOLLECTIONID\n" +
                        "    FROM OPENCPS_DICTCOLLECTION\n" +
                        "    WHERE COLLECTIONCODE = 'GOVERNMENT_AGENCY'\n" +
                        ")\n" +
                        "ORDER BY LEVEL_, ITEMNAME";

                allItems.addAll(executeQueryForDictItem(sql));
            }
        }

        return allItems;
    }

    private List<DictItem> executeQueryForDictItem(String sql) {
        List<DictItem> dictItems = new ArrayList<>();
        DataSource dataSource = InfrastructureUtil.getDataSource();

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement pst = connection.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()
        ) {
            while (rs.next()) {
                DictItem dictItem = DictItemLocalServiceUtil.createDictItem(rs.getLong("DICTITEMID"));
                dictItem.setItemCode(rs.getString("ITEMCODE"));
                dictItem.setItemName(rs.getString("ITEMNAME"));
                dictItem.setParentItemId(rs.getLong("PARENTITEMID"));
                dictItem.setLevel_(rs.getInt("LEVEL_"));
                dictItems.add(dictItem);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Có thể thay bằng logger
        }

        return dictItems;
    }


    private List<DictItem> statisticDictItembk(String scope) {
        String sql = "SELECT distinct DICTITEMID, ITEMCODE, ITEMNAME, PARENTITEMID, LEVEL_\n" +
                "FROM OPENCPS_DICTITEM\n" +
                "START WITH ITEMCODE IN (\n" +
                "    SELECT GOVAGENCYCODE\n" +
                "    FROM FILESTOREGOV f\n" +
                "    WHERE f.ISACTIVE = 1\n" +
                "      AND f.GOVAGENCYCODE IS NOT NULL\n" +
                ")\n" +
                "AND DICTCOLLECTIONID IN (\n" +
                "    SELECT DICTCOLLECTIONID\n" +
                "    FROM OPENCPS_DICTCOLLECTION\n" +
                "    WHERE COLLECTIONCODE = 'GOVERNMENT_AGENCY'\n" +
                ")\n" +
                "CONNECT BY PRIOR PARENTITEMID = DICTITEMID order by LEVEL_, ITEMNAME";
        if (scope != null) {
            sql = "SELECT DISTINCT DICTITEMID, ITEMCODE, ITEMNAME, PARENTITEMID, LEVEL_\n" +
                    "FROM OPENCPS_DICTITEM\n" +
                    "WHERE ITEMCODE IN (\n" +
                    "    SELECT GOVAGENCYCODE\n" +
                    "    FROM FILESTOREGOV f\n" +
                    "    WHERE f.ISACTIVE = 1\n" +
                    "      AND f.GOVAGENCYCODE IS NOT NULL\n" +
                    "      AND f.GOVAGENCYCODE in (" + scope + ")" +
                    ")\n" +
                    "AND DICTCOLLECTIONID IN (\n" +
                    "    SELECT DICTCOLLECTIONID\n" +
                    "    FROM OPENCPS_DICTCOLLECTION\n" +
                    "    WHERE COLLECTIONCODE = 'GOVERNMENT_AGENCY'\n" +
                    ")\n" +
                    "ORDER BY LEVEL_, ITEMNAME";
        }
        DataSource dataSource = InfrastructureUtil.getDataSource();
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement pst = connection.prepareStatement(sql)
        ) {
            try (ResultSet rs = pst.executeQuery()) {
                List<DictItem> dictItems = new ArrayList<>();

                while (rs.next()) {
                    DictItem dictItem = DictItemLocalServiceUtil.createDictItem(rs.getLong("DICTITEMID"));
                    dictItem.setItemCode(rs.getString("ITEMCODE"));
                    dictItem.setItemName(rs.getString("ITEMNAME"));
                    dictItem.setParentItemId(rs.getLong("PARENTITEMID"));
                    dictItem.setLevel_(rs.getInt("LEVEL_"));
                    dictItems.add(dictItem);
                }
                return dictItems;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private Map<Long, List<DictItem>> tree(List<DictItem> dictItems) {
        Map<Long, List<DictItem>> tree = new HashMap<>();
        for (DictItem item : dictItems) {
            tree.computeIfAbsent(item.getParentItemId(), k -> new ArrayList<>()).add(item);
        }
        return tree;
    }

    private long countTotal(
            long dictItemId,
            Map<Long, List<DictItem>> tree,
            Map<String, JSONObject> directFileCount, String scope) {

        long count = 0;

        // Lấy itemCode
        String itemCode = null;
        try {
            itemCode = DictItemLocalServiceUtil.getDictItem(dictItemId).getItemCode();
        } catch (PortalException e) {
            throw new RuntimeException(e);
        }
        JSONObject file = directFileCount.get(itemCode);
        if (file != null) {
            count += file.getLong("total");
        }
        // Duyệt các con (nếu có)
        List<DictItem> children = tree.get(dictItemId);
        if (children != null) {
            for (DictItem child : children) {
                count += countTotal(child.getDictItemId(), tree, directFileCount, scope);
            }
        }
        return count;
    }


    private long countTotalUsage(
            long dictItemId,
            Map<Long, List<DictItem>> tree,
            Map<String, JSONObject> directFileCount, String scope) {

        String itemCode = null;
        try {
            itemCode = DictItemLocalServiceUtil.getDictItem(dictItemId).getItemCode();
        } catch (PortalException e) {
            throw new RuntimeException(e);
        }
        long count = 0;

        JSONObject file = directFileCount.get(itemCode);
        if (file != null) {
            count += file.getLong("totalUsage");
        }
        List<DictItem> children = tree.get(dictItemId);
        if (children != null) {
            for (DictItem child : children) {
                count += countTotalUsage(child.getDictItemId(), tree, directFileCount, scope);
            }
        }
        return count;
    }

    private long countDossierNo(
            long dictItemId,
            Map<Long, List<DictItem>> tree,
            Map<String, JSONObject> directFileCount, String scope) {

        String itemCode = null;
        try {
            itemCode = DictItemLocalServiceUtil.getDictItem(dictItemId).getItemCode();
        } catch (PortalException e) {
            throw new RuntimeException(e);
        }
        long count = 0;

        JSONObject file = directFileCount.get(itemCode);
        if (file != null) {
            count += file.getLong("dossierNoCount");
        }

        List<DictItem> children = tree.get(dictItemId);
        if (children != null) {
            for (DictItem child : children) {
                count += countDossierNo(child.getDictItemId(), tree, directFileCount, scope);
            }
        }
        return count;
    }

    private JSONArray calculateResult(List<DictItem> dictItems, Map<Long, List<DictItem>> tree, Map<String,
            JSONObject> directFileCount, int maxStatisticLevel, String scope) {

        JSONArray jsonArray = JSONFactoryUtil.createJSONArray();
        Map<String, JSONObject> resultMap = new HashMap<>();
        for (DictItem item : dictItems) {
            String itemCode = item.getItemCode();
            if (!resultMap.containsKey(itemCode) && item.getLevel_() < maxStatisticLevel && !itemCode.equalsIgnoreCase("000.00.00.G11")) {
                long total = countTotal(item.getDictItemId(), tree, directFileCount, scope);
                long totalUsage = countTotalUsage(item.getDictItemId(), tree, directFileCount, scope);
                long dossierNoCount = countDossierNo(item.getDictItemId(), tree, directFileCount, scope);
                JSONObject totalResult = JSONFactoryUtil.createJSONObject();
                totalResult.put(FileStoreGovTerm.GOVAGENCYNAME, item.getItemName());
                totalResult.put(FileStoreGovTerm.GOVAGENCYCODE, item.getItemCode());
                totalResult.put(FileStoreGovTerm.TOTAL, total);
                totalResult.put(FileStoreGovTerm.TOTAL_USAGE, totalUsage);
                totalResult.put(FileStoreGovTerm.DOSSIERNO_COUNT, dossierNoCount);
                jsonArray.put(totalResult);
                resultMap.put(item.getItemCode(), totalResult);
            }
        }
        return jsonArray;
    }

    private JSONArray statisticDepartments(String scope) {
        final int BATCH_SIZE = 500;
        JSONArray results = JSONFactoryUtil.createJSONArray();

        if (scope != null) {
            // Tách danh sách mã
            List<String> govCodes = Arrays.stream(scope.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty()).distinct()
                    .collect(Collectors.toList());

            // Chia thành từng batch
            for (int i = 0; i < govCodes.size(); i += BATCH_SIZE) {
                int end = Math.min(i + BATCH_SIZE, govCodes.size());
                List<String> batch = govCodes.subList(i, end);
                String inClause = String.join(",", batch);

                String sql = "SELECT f.GOVAGENCYCODE AS GOVAGENCYCODE,\n" +
                        "       COUNT(DISTINCT f.FILESTOREGOVID) AS SOGIAYTOSOHOA,\n" +
                        "       COUNT(DISTINCT f.DOSSIERNO) AS SOLUONGHOSODASOHOA,\n" +
                        "       COUNT(CASE WHEN fuh.ACTIONSS = 'Tái sử dụng' THEN fuh.FILESTOREGOVID END) AS SOLANSUDUNG\n" +
                        "FROM FILESTOREGOV f\n" +
                        "LEFT JOIN FILEGOVUSEDHISTORY fuh ON f.FILESTOREGOVID = fuh.FILESTOREGOVID\n" +
                        "WHERE f.ISACTIVE = 1\n" +
                        "  AND f.CREATEDATE is not null  \n" +
                        "  AND f.GOVAGENCYCODE IS NOT NULL\n" +
                        "  AND f.GOVAGENCYCODE IN (" + inClause + ")\n" +
                        " GROUP BY f.GOVAGENCYCODE";

                queryAndAppendToJSONArray(sql, results);
            }
        } else {
            // Không có scope -> lấy tất cả
            String sql = "SELECT f.GOVAGENCYCODE AS GOVAGENCYCODE,\n" +
                    "       COUNT(DISTINCT f.FILESTOREGOVID) AS SOGIAYTOSOHOA,\n" +
                    "       COUNT(DISTINCT f.DOSSIERNO) AS SOLUONGHOSODASOHOA,\n" +
                    "       COUNT(CASE WHEN fuh.ACTIONSS = 'Tái sử dụng' THEN fuh.FILESTOREGOVID END) AS SOLANSUDUNG\n" +
                    "FROM FILESTOREGOV f\n" +
                    "LEFT JOIN FILEGOVUSEDHISTORY fuh ON f.FILESTOREGOVID = fuh.FILESTOREGOVID\n" +
                    "WHERE f.ISACTIVE = 1\n" +
                    "  AND f.GOVAGENCYCODE IS NOT NULL\n" +
                    "  AND f.CREATEDATE is not null  \n" +
                    " GROUP BY f.GOVAGENCYCODE";

            queryAndAppendToJSONArray(sql, results);
        }

        return results;
    }

    private void queryAndAppendToJSONArray(String sql, JSONArray results) {
        DataSource dataSource = InfrastructureUtil.getDataSource();
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement pst = connection.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()
        ) {
            while (rs.next()) {
                JSONObject result = JSONFactoryUtil.createJSONObject();
                result.put("govAgencyCode", rs.getString("GOVAGENCYCODE"));
                result.put("total", rs.getLong("SOGIAYTOSOHOA"));
                result.put("totalUsage", rs.getLong("SOLANSUDUNG"));
                result.put("dossierNoCount", rs.getLong("SOLUONGHOSODASOHOA"));
                results.put(result);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void updateJsonList(List<JSONObject> jsonList, List<DictItem> sortedDictItems) {
        // Bước 1: Tạo Set chứa các govAgencyCode đã có trong jsonList
        Set<String> existingCodes = new HashSet<>();
        Map<String, JSONObject> jsonListMap = new HashMap<>();
        for (JSONObject obj : jsonList) {
            existingCodes.add((String) obj.get("govAgencyCode"));
            jsonListMap.put((String) obj.get("govAgencyCode"), obj);
        }

        // Bước 2: Duyệt sortedDictItems và kiểm tra xem đã có trong existingCodes chưa
        for (DictItem dictItem : sortedDictItems) {
            String itemCode = dictItem.getItemCode();
            if (!existingCodes.contains(itemCode)) {
                // Nếu chưa có, tạo JSONObject mới
                JSONObject newObj = JSONFactoryUtil.createJSONObject();
                newObj.put("govAgencyCode", itemCode);
                newObj.put("total", 0);        // Gán mặc định total = 0
                newObj.put("totalUsage", 0);   // Gán mặc định totalUsage = 0

                jsonList.add(newObj); // Thêm vào danh sách jsonList
            }
            if (!existingCodes.contains(itemCode)) {
                JSONObject newObj = JSONFactoryUtil.createJSONObject();
                int sumTotal = 0;
                int sumTotalUsage = 0;
                int sumDossierNoCount = 0;
                JSONObject oldObject = jsonListMap.get(itemCode);
                if (oldObject != null) {
                    newObj.put("govAgencyCode", itemCode);
                    newObj.put("total", sumTotal += oldObject.getInt("total"));
                    newObj.put("totalUsage", sumTotalUsage += oldObject.getInt("totalUsage"));
                    newObj.put("dossierNoCount", sumDossierNoCount += oldObject.getInt("dossierNoCount"));
                }

                jsonList.add(newObj);
            }

        }
    }

    public JSONArray sortItemCodes(List<DictItem> sortedDictItems, JSONArray itemCodes) {

        // Bước 2: Tạo bản đồ ánh xạ itemCode sang chỉ số trong danh sách đã sắp xếp
        Map<String, Integer> codeToIndex = new HashMap<>();
        for (int i = 0; i < sortedDictItems.size(); i++) {
            codeToIndex.put(sortedDictItems.get(i).getItemCode(), i);
        }

        // Bước 3: Chuyển JSONArray thành List<JSONObject> để sắp xếp
        List<JSONObject> jsonList = new ArrayList<>();
        for (int i = 0; i < itemCodes.length(); i++) {
            jsonList.add(itemCodes.getJSONObject(i));
        }

        updateJsonList(jsonList, sortedDictItems);

        // Bước 4: Sắp xếp List<JSONObject> dựa trên chỉ số của itemCode
        jsonList.sort((json1, json2) -> {
            String code1 = json1.getString("govAgencyCode");
            String code2 = json2.getString("govAgencyCode");
            Integer index1 = codeToIndex.getOrDefault(code1, Integer.MAX_VALUE);
            Integer index2 = codeToIndex.getOrDefault(code2, Integer.MAX_VALUE);
            return index1.compareTo(index2);
        });

        // Bước 5: Tạo JSONArray mới từ danh sách đã sắp xếp
        JSONArray sortedArray = JSONFactoryUtil.createJSONArray();
        for (JSONObject json : jsonList) {
            sortedArray.put(json);
        }

        return sortedArray;
    }

    private JSONArray getDataLine(DictItem dictItem, LocalDateTime fromDate, LocalDateTime toDate) throws java.text.ParseException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String itemCode = dictItem == null ? null : dictItem.getItemCode();
        String from = fromDate.format(dtf);
        String to = toDate.format(dtf);
        String sql = "WITH itemlist AS\n" +
                "  (\n" +
                "SELECT\n" +
                "  di.dictitemid,\n" +
                "  di.itemcode,\n" +
                "  di.itemname,\n" +
                "  di.parentitemid\n" +
                "FROM\n" +
                "  OPENCPS_DICTITEM di\n" +
                "START WITH\n" +
                "  di.itemcode = ? \n" +
                "CONNECT BY\n" +
                "  PRIOR di.dictitemid = di.parentitemid),\n" +
                " used_count AS \n" +
                " (\n" +
                "SELECT\n" +
                "  f2.GOVAGENCYCODE, FILEGOVUSEDHISTORYID \n" +
                "FROM\n" +
                "  FILEGOVUSEDHISTORY f\n" +
                "LEFT JOIN FILESTOREGOV f2 ON\n" +
                "  f.FILESTOREGOVID = f2.FILESTOREGOVID\n" +
                "WHERE\n" +
                "  f.ACTIONSS = 'Tái sử dụng'\n" +
                "  AND f2.ISACTIVE = 1)\n" +
                "SELECT * FROM (SELECT\n" +
                "  COALESCE (f.GOVAGENCYCODE, ' ') AS MaXa,\n" +
                "  COALESCE (t.itemname, 'TongCong') AS Donvi,\n" +
                "  COUNT(DISTINCT f.FILESTOREGOVID) AS SoGiaytoSoHoa,\n" +
                "  COUNT(DISTINCT FILEGOVUSEDHISTORYID) AS SoLanSuDung\n" +
                "FROM\n" +
                "  filestoregov f\n" +
                "RIGHT JOIN itemlist t ON\n" +
                "  f.govagencycode = t.itemcode\n" +
                "left JOIN used_count uc ON\n" +
                "  uc.govagencycode = t.itemcode\n" +
                "WHERE\n" +
                "  f.ISACTIVE = 1\n" +
                "AND f.CREATEDATE BETWEEN TO_DATE(?, 'yyyy-MM-dd HH24:MI:SS') AND TO_DATE(?, 'yyyy-MM-dd HH24:MI:SS')" +
                "GROUP BY\n" +
                "  GROUPING SETS (\n" +
                "  (f.GOVAGENCYCODE,\n" +
                "  t.itemname),\n" +
                "  ()\n" +
                "  ))\n" +
                "ORDER BY SoGiaytoSoHoa DESC";

        DataSource dataSource = InfrastructureUtil.getDataSource();
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement pst = connection.prepareStatement(sql)
        ) {
            pst.setString(1, itemCode);
            pst.setString(2, from);
            pst.setString(3, to);
            try (ResultSet rs = pst.executeQuery()) {

                JSONArray results = JSONFactoryUtil.createJSONArray();

                while (rs.next()) {

                    JSONObject result = JSONFactoryUtil.createJSONObject();
                    if (rs.getString("DONVI").equalsIgnoreCase("TongCong")) {
                        result.put("itemName", "Tổng cộng");
                    } else {
                        result.put("itemName", rs.getString("DONVI"));
                    }
                    result.put("itemCode", rs.getString("MAXA"));
                    result.put("total", rs.getLong("SOGIAYTOSOHOA"));
                    result.put("totalUsage", rs.getLong("SOLANSUDUNG"));

                    results.put(result);
                }
                return results;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public static String convertToLuceneDate(String dateStr) throws java.text.ParseException {
        DateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        DateFormat luceneFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        Date date = inputFormat.parse(dateStr);
        return luceneFormat.format(date);
    }

    @Override
    public Response updateDossierInfo(HttpServletRequest request, HttpHeaders header, Company company, Locale locale, User user, ServiceContext serviceContext, RecipientInputModel model, String dossierNo, String dossierName) {
        BackendAuth auth = new BackendAuthImpl();
        if (!auth.isAuth(serviceContext)) {
            return Response.status(HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION).entity(MessageUtil.getMessage(ConstantUtils.API_JSON_MESSAGE_NONAUTHORATIVE))
                    .build();
        }
        Date now = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String strDate = dateFormat.format(now);
        long[] fileStoreIdList = model.getFileStoreIDList();
        StringBuilder responseMessage = new StringBuilder();
        for (Long fileStoreId : fileStoreIdList) {
            FileStoreGov updateFileStoreGov = FileStoreGovLocalServiceUtil.fetchFileStoreGov(fileStoreId);
            Response fileStoreGovValidator = FileStoreGovValidator.validateActions(fileStoreId, user.getUserId(), updateFileStoreGov, serviceContext);
            if (fileStoreGovValidator.getStatus() != HttpStatus.SC_OK) {
                return Response.status(fileStoreGovValidator.getStatus())
                        .entity(fileStoreGovValidator.getEntity()).build();
            }
            if (Validator.isNull(updateFileStoreGov)) {
                responseMessage.append("Không tìm thấy giấy tờ : ").append(fileStoreId).append("\n");
                continue;

            }
            if (updateFileStoreGov.getDossierNo() != null || updateFileStoreGov.getDossierName() != null) {
                responseMessage.append("Giấy tờ ").append(updateFileStoreGov.getFileName()).append(" đã được gán mã hồ sơ trước đó.").append("\n");
            }
            try {
                updateFileStoreGov.setModifiedDate(APIDateTimeUtils._stringToDate(strDate, "dd/MM/yyyy HH:mm:ss"));
                if (Validator.isNotNull(dossierNo) && !dossierNo.isEmpty())
                    updateFileStoreGov.setDossierNo(dossierNo);
                if (Validator.isNotNull(dossierName) && !dossierName.isEmpty())
                    updateFileStoreGov.setDossierName(dossierName);
                FileStoreGovLocalServiceUtil.updateFileStoreGov(updateFileStoreGov);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Response.status(HttpStatus.SC_OK).entity(responseMessage.toString()).build();
    }

    @Override
    public Response getDomainFileStoreGov(
            HttpServletRequest request, HttpHeaders header, Company company,
            Locale locale, User user, ServiceContext serviceContext, String govAgencyCode) {

        JSONArray results = JSONFactoryUtil.createJSONArray();

        long groupId = GetterUtil.getLong(header.getHeaderString(Field.GROUP_ID));
        Map<String, Object> params = createParams(govAgencyCode, user, groupId);
        String allGovs = (String) params.get(FileStoreGovTerm.GOVAGENCYCODE);
        // Nếu không có giá trị -> lấy tất cả domain
        if (Validator.isNull(allGovs)) {
            String sql = "SELECT di.ITEMCODE, di.ITEMNAME\n" +
                    "FROM OPENCPS_DICTITEM di\n" +
                    "JOIN OPENCPS_DICTCOLLECTION dc\n" +
                    "  ON di.DICTCOLLECTIONID = dc.DICTCOLLECTIONID\n" +
                    "JOIN OPENCPS_SERVICEINFO si\n" +
                    "  ON di.ITEMCODE = si.DOMAINCODE\n" +
                    "JOIN FILESTOREGOV fg\n" +
                    "  ON si.SERVICECODE = fg.SERVICECODE\n" +
                    "WHERE dc.COLLECTIONCODE = 'SERVICE_DOMAIN' AND fg.ISACTIVE = 1 and fg.GOVAGENCYCODE is not null \n" +
                    "   GROUP BY di.ITEMCODE, di.ITEMNAME";
            queryAndAppendFileStoreGovDomain(sql, results);
        } else {
            // Tách danh sách mã GOV, lọc trùng và chia batch 900 phần tử
            List<String> govList = Arrays.stream(allGovs.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .distinct()
                    .collect(Collectors.toList());

            int batchSize = 900;
            for (int i = 0; i < govList.size(); i += batchSize) {
                List<String> batch = govList.subList(i, Math.min(i + batchSize, govList.size()));

                // Thêm dấu nháy đơn cho từng giá trị
                String inClause = batch.stream()
                        .map(gov -> "'" + gov + "'")
                        .collect(Collectors.joining(","));

                String sql = "SELECT di.ITEMCODE, di.ITEMNAME\n" +
                        "FROM OPENCPS_DICTITEM di\n" +
                        "JOIN OPENCPS_DICTCOLLECTION dc\n" +
                        "  ON di.DICTCOLLECTIONID = dc.DICTCOLLECTIONID\n" +
                        "JOIN OPENCPS_SERVICEINFO si\n" +
                        "  ON di.ITEMCODE = si.DOMAINCODE\n" +
                        "JOIN FILESTOREGOV fg\n" +
                        "  ON si.SERVICECODE = fg.SERVICECODE\n" +
                        "WHERE dc.COLLECTIONCODE = 'SERVICE_DOMAIN'\n" +
                        "  AND fg.GOVAGENCYCODE in (" + inClause + ") AND fg.ISACTIVE = 1 GROUP BY di.ITEMCODE, di.ITEMNAME";

                queryAndAppendFileStoreGovDomain(sql, results);
            }
        }

        // Có thể trả về kết quả JSON
        return Response.ok(results.toJSONString(), MediaType.APPLICATION_JSON).build();
    }

    private LinkedHashMap<String, Object> createParams(String govAgencyCode, User user, long groupId) {
        Employee employee = EmployeeLocalServiceUtil.fetchByFB_MUID(user.getUserId());
        LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
        if (Validator.isNotNull(employee)) {
            if (Validator.isNotNull(employee.getScope())) {
                DictCollection dictCollection = DictCollectionLocalServiceUtil.fetchByF_dictCollectionCode(GOVERNMENT_AGENCY, groupId);
                if (Validator.isNotNull(dictCollection)) {
                    DictItem dictItem = DictItemLocalServiceUtil.fetchByF_dictItemCode(employee.getScope(), dictCollection.getDictCollectionId(), groupId);
                    _log.info("search:: dictItem = " + dictItem);
                    String scopeList = null;
                    if (Validator.isNotNull(dictItem)) {
                        scopeList = ScopeUtils.getAllSubUnit(dictItem, dictCollection.getDictCollectionId(), groupId);
                        _log.info("search:: scopeList = " + scopeList);
                    }
                    if (scopeList != null) {
                        params.put(FileStoreGovTerm.GOVAGENCYCODE, scopeList);
                    } else {
                        params.put(FileStoreGovTerm.GOVAGENCYCODE, employee.getScope());
                    }
                }
            }
        }
        if (Validator.isNotNull(govAgencyCode)) {
            String[] agencyList = govAgencyCode.split(",");
            StringBuilder scopeList = new StringBuilder();

            for (int i = 0; i < agencyList.length; i++) {
                DictCollection dictCollection = DictCollectionLocalServiceUtil.fetchByF_dictCollectionCode(GOVERNMENT_AGENCY, groupId);
                if (Validator.isNotNull(dictCollection)) {
                    DictItem dictItem = DictItemLocalServiceUtil.fetchByF_dictItemCode(agencyList[i], dictCollection.getDictCollectionId(), groupId);
                    scopeList.append(dictItem.getItemCode());
                }
            }
            params.put(FileStoreGovTerm.GOVAGENCYCODE, scopeList.toString());
        }
        return params;
    }

    @Override
    public Response addFileStoreGov(HttpServletRequest request, HttpHeaders header, Company company, Locale locale, User user, ServiceContext serviceContext,
                                    FileStoreGovRequest fileStoreGovRequest) {
        BackendAuth auth = new BackendAuthImpl();
        if (!auth.isAuth(serviceContext)) {
            return Response.status(HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION).entity(new QLCDResponse(QLCDConstants.CODE_FAIL, "You are not authorized to perform this action")).build();
        }

        List<FileStoreGovRequest.FileItem> items = new ArrayList<>();

        for (int i = 0; ; i++) {
            String prefix = "files[" + i + "].";
            String fileName = ParamUtil.getString(request, prefix + "fileName", null);
            if (fileName == null) break;
            FileStoreGovRequest.FileItem item = new FileStoreGovRequest.FileItem();
            item.setFileName(fileName);
            item.setIssueDate(ParamUtil.getString(request, prefix + "issueDate"));
            item.setCodeNumber(ParamUtil.getString(request, prefix + "codeNumber"));
            item.setCodeNotation(ParamUtil.getString(request, prefix + "codeNotation"));
            item.setDepartmentIssue(ParamUtil.getString(request, prefix + "departmentIssue"));
            item.setOtherFileName(ParamUtil.getString(request, prefix + "otherFileName"));
            item.setOtherDepartmentIssue(ParamUtil.getString(request, prefix + "otherDepartmentIssue"));
            item.setAbstractSS(ParamUtil.getString(request, prefix + "abstractSS"));
            item.setValidTo(ParamUtil.getString(request, prefix + "validTo"));
            item.setValidScope(ParamUtil.getString(request, prefix + "validScope"));
            item.setPartNo(ParamUtil.getString(request, prefix + "partNo"));
            item.setPartType(ParamUtil.getString(request, prefix + "partType"));
            item.setNewFileEntryId(ParamUtil.getLong(request, prefix + "newFileEntryId"));
            item.setFileStoreGovId(ParamUtil.getLong(request, prefix + "fileStoreGovId"));
            item.setDisplayName(ParamUtil.getString(request, prefix + "displayName"));
            item.setIsSigned(ParamUtil.getBoolean(request, prefix + "isSign"));
            items.add(item);
        }
        fileStoreGovRequest.setFiles(items);

        long groupId = GetterUtil.getLong(header.getHeaderString(Field.GROUP_ID));
        Employee employee = EmployeeLocalServiceUtil.fetchByFB_MUID(user.getUserId());
        Date now = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String strDate = dateFormat.format(now);
        for (FileStoreGovRequest.FileItem fileDetail : fileStoreGovRequest.getFiles()) {
            FileStoreGov fileStoreGov;
            // Check exist -> update existing | Not exist -> create new one
            if (fileDetail.getFileStoreGovId() != null && fileDetail.getFileStoreGovId() != 0) {
                fileStoreGov = FileStoreGovLocalServiceUtil.fetchFileStoreGov(fileDetail.getFileStoreGovId());
            } else {
                long id = CounterLocalServiceUtil.increment(FileStoreGov.class.getName());
                fileStoreGov = FileStoreGovLocalServiceUtil.createFileStoreGov(id);
                fileStoreGov.setCreateDate(APIDateTimeUtils._stringToDate(strDate, "dd/MM/yyyy HH:mm:ss"));
                fileStoreGov.setModifiedDate(APIDateTimeUtils._stringToDate(strDate, "dd/MM/yyyy HH:mm:ss"));
                fileStoreGov.setGroupId(groupId);
                fileStoreGov.setFileEntryId(fileDetail.getNewFileEntryId());
                fileStoreGov.setGovAgencyCode(employee.getScope());
            }
            if (Validator.isNotNull(fileDetail.getNewFileEntryId())) {
                FileEntry fileEntry;
                try {
                    fileEntry = DLAppLocalServiceUtil.getFileEntry(fileDetail.getNewFileEntryId());
                } catch (Exception e) {
                    return Response.status(org.apache.commons.httpclient.util.HttpURLConnection.HTTP_BAD_REQUEST).entity("Có lỗi xảy ra trong quá trình xử lý").build();
                }

                if (Validator.isNotNull(employee)) {
                    fileStoreGov.setEmployeeId(employee.getEmployeeId());
                }
                if (Validator.isNotNull(fileStoreGovRequest.getOwnerDate())) {
                    fileStoreGov.setOwnerDate(fileStoreGovRequest.getOwnerDate());
                }
                if (Validator.isNotNull(fileStoreGovRequest.getOwnerName())) {
                    fileStoreGov.setOwnerName(fileStoreGovRequest.getOwnerName());
                }
                if (Validator.isNotNull(fileStoreGovRequest.getOwnerNo())) {
                    fileStoreGov.setOwnerNo(fileStoreGovRequest.getOwnerNo());
                }
                if (Validator.isNotNull(fileStoreGovRequest.getOwnerType())) {
                    fileStoreGov.setOwnerType(fileStoreGovRequest.getOwnerType());
                }
                fileStoreGov.setPartNo(fileDetail.getPartNo());
                fileStoreGov.setFileName(fileDetail.getFileName());
                fileStoreGov.setDisplayName(fileEntry.getFileName());
                fileStoreGov.setServiceCode(fileStoreGovRequest.getServiceCode());
                fileStoreGov.setCodeNumber(fileDetail.getCodeNumber());
                fileStoreGov.setCodeNotation(fileDetail.getCodeNotation());
                if (!fileDetail.getDepartmentIssue().isEmpty()) {
                    fileStoreGov.setDepartmentIssue(fileDetail.getDepartmentIssue());
                } else {
                    fileStoreGov.setDepartmentIssue(fileDetail.getOtherDepartmentIssue());
                }
                if (!fileDetail.getFileName().isEmpty()) {
                    fileStoreGov.setFileName(fileDetail.getFileName());
                } else {
                    fileStoreGov.setFileName(fileDetail.getOtherFileName());
                }
                fileStoreGov.setAbstractSS(fileDetail.getAbstractSS());
                fileStoreGov.setValidTo(APIDateTimeUtils._stringToDate(fileDetail.getValidTo(), "dd/MM/yyyy"));
                fileStoreGov.setValidScope(fileDetail.getValidScope());
                fileStoreGov.setIssueDate(APIDateTimeUtils._stringToDate(fileDetail.getIssueDate(), "dd/MM/yyyy"));
                fileStoreGov.setSize_(fileEntry.getSize());
                fileStoreGov.setPartType(Long.parseLong((fileDetail.getPartType())));
//                fileStoreGov.setPartTypeDetail(Long.parseLong(fileDetail.getPartTypeDetail()));
                fileStoreGov.setShared(1);
                fileStoreGov.setIsActive(1);
                fileStoreGov.setDossierNo(fileStoreGovRequest.getDossierNo());
                String fileGovCode = generateFileGovCode(fileStoreGov.getOwnerNo(),
                        fileStoreGov.getOwnerName(), fileStoreGov.getOwnerDate(), fileStoreGov.getServiceCode(),
                        fileStoreGov.getPartNo(), Integer.valueOf(String.valueOf(fileStoreGov.getPartType())),
                        fileStoreGov.getCodeNumber(), fileStoreGov.getCodeNotation(), fileStoreGov.getDossierNo());


                fileStoreGov.setFileGovCode(fileGovCode);
                String typeNo = generateTypeNo(Integer.parseInt((String.valueOf(fileStoreGov.getPartType()))), fileStoreGov.getPartNo(), fileStoreGov.getServiceCode());
                fileStoreGov.setTypeNo(typeNo);
                FileStoreGovLocalServiceUtil.updateFileStoreGov(fileStoreGov);

                if (Validator.isNotNull(fileDetail.getOtherDepartmentIssue())) {
                    saveToOtherDictItem(fileDetail.getOtherDepartmentIssue(), fileDetail.getFileStoreGovId(), employee.getEmployeeId());
                }
                if (Validator.isNotNull(fileDetail.getOtherFileName())) {
                    saveToOtherDictItem(fileDetail.getOtherFileName(), fileDetail.getFileStoreGovId(), employee.getEmployeeId());
                }
            }
        }
        return Response.status(HttpURLConnection.HTTP_OK).build();
    }



    @Override
    public Response getSuggestDossierNo(HttpServletRequest request, HttpHeaders header, Company company, Locale locale,
                                        User user, ServiceContext serviceContext, String fromDate, String toDate,
                                        String keyword) {
        BackendAuth auth = new BackendAuthImpl();
        //check authen
        if (!auth.isAuth(serviceContext)) {
            return Response.status(HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION).entity(new QLCDResponse(QLCDConstants.CODE_FAIL, "You are not authorized to perform this action")).build();
        }
        long groupId = GetterUtil.getLong(header.getHeaderString(Field.GROUP_ID));
        Employee employee = EmployeeLocalServiceUtil.fetchByFB_MUID(user.getUserId());
        String scope = employee.getScope();
        scope = getFirstItem(scope);
        DictCollection dictCollection = DictCollectionLocalServiceUtil.fetchByF_dictCollectionCode(GOVERNMENT_AGENCY, groupId);
        List<DictItem> dictItems = getParentsAndChildrenByItemCode(scope, dictCollection.getDictCollectionId(), groupId);
        DictItem level1Item = getLevel1Item(dictItems);
        if (level1Item == null) {
            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(new QLCDResponse(QLCDConstants.CODE_FAIL, "Level 2 dict item not found.")).build();
        }
        String level1ItemCode = level1Item.getItemCode();

        List<FileStoreGovResponse> suggestFileStoreGovs = suggestFileStoreGovs(employee.getEmployeeId(), level1ItemCode, fromDate, toDate, keyword);
        String generateDossierNo = generateDossierNo(getLatestDossierNo(level1ItemCode), level1ItemCode);


        SuggestDossierNoResponse suggestDossierNoResponse = new SuggestDossierNoResponse();
        suggestDossierNoResponse.setGenerateDossierNo(generateDossierNo);
        suggestDossierNoResponse.setSuggestDossierNoList(suggestFileStoreGovs);

        String json = new Gson().toJson(suggestDossierNoResponse);
        return Response.status(HttpURLConnection.HTTP_OK).entity(json).build();
    }

    public static List<DictItem> getParentsAndChildrenByItemCode(String govAgencyCode, long dictCollectionId, long groupId) {
        List<DictItem> dictItems = new ArrayList<>();
        String sql = "WITH parents (DICTITEMID, PARENTITEMID, ITEMCODE, ITEMNAME, LEVEL_, GROUPID, DICTCOLLECTIONID) AS (\n" +
                "  SELECT DICTITEMID, PARENTITEMID, ITEMCODE, ITEMNAME, LEVEL_, GROUPID, DICTCOLLECTIONID\n" +
                "  FROM OPENCPS_DICTITEM\n" +
                "  WHERE ITEMCODE = ?\n" +
                "    AND GROUPID = ?\n" +
                "    AND DICTCOLLECTIONID = ?\n" +
                "  UNION ALL\n" +
                "  SELECT p.DICTITEMID, p.PARENTITEMID, p.ITEMCODE, p.ITEMNAME, p.LEVEL_, p.GROUPID, p.DICTCOLLECTIONID\n" +
                "  FROM OPENCPS_DICTITEM p\n" +
                "  INNER JOIN parents c ON c.PARENTITEMID = p.DICTITEMID\n" +
                "),\n" +
                "children (DICTITEMID, PARENTITEMID, ITEMCODE, ITEMNAME, LEVEL_, GROUPID, DICTCOLLECTIONID) AS (\n" +
                "  SELECT DICTITEMID, PARENTITEMID, ITEMCODE, ITEMNAME, LEVEL_, GROUPID, DICTCOLLECTIONID\n" +
                "  FROM OPENCPS_DICTITEM\n" +
                "  WHERE ITEMCODE = ?\n" +
                "    AND   GROUPID = ?\n" +
                "    AND DICTCOLLECTIONID = ?\n" +
                "  UNION ALL\n" +
                "  SELECT c.DICTITEMID, c.PARENTITEMID, c.ITEMCODE, c.ITEMNAME, c.LEVEL_, c.GROUPID, c.DICTCOLLECTIONID\n" +
                "  FROM OPENCPS_DICTITEM c\n" +
                "  INNER JOIN children p ON c.PARENTITEMID = p.DICTITEMID\n" +
                ")\n" +
                "SELECT * FROM parents\n" +
                "UNION\n" +
                "SELECT * FROM children";

        DataSource dataSource = InfrastructureUtil.getDataSource();
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement pst = connection.prepareStatement(sql)
        ) {
            pst.setString(1, govAgencyCode);
            pst.setLong(2, groupId);
            pst.setLong(3, dictCollectionId);
            pst.setString(4, govAgencyCode);
            pst.setLong(5, groupId);
            pst.setLong(6, dictCollectionId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    DictItem dictItem = DictItemLocalServiceUtil.createDictItem(rs.getInt("DICTITEMID"));
                    dictItem.setParentItemId(rs.getInt("PARENTITEMID"));
                    dictItem.setItemCode(rs.getString("ITEMCODE"));
                    dictItem.setItemName(rs.getString("ITEMNAME"));
                    dictItem.setLevel_(rs.getInt("LEVEL_"));
                    dictItems.add(dictItem);
                }
            }
            return dictItems;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dictItems;
    }


    private String getLatestDossierNo(String itemCode) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
        String todayDate = today.format(formatter);

        List<String> latestDossierNoList = new ArrayList<>();
        String conditionValue = StringPool.PERCENT.concat(itemCode.concat(StringPool.DASH).concat(todayDate)).concat(StringPool.PERCENT);

        String sql = "select distinct DOSSIERNO from FILESTOREGOV where DOSSIERNO like ?";
        DataSource dataSource = InfrastructureUtil.getDataSource();
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement pst = connection.prepareStatement(sql)
        ) {
            pst.setString(1, conditionValue);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    latestDossierNoList.add(rs.getString("DOSSIERNO"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        String maxString = null;
        int maxValue = Integer.MIN_VALUE;
        for (String str : latestDossierNoList) {
            String index = str.split("-")[2];
            int value = Integer.parseInt(index);
            if (value > maxValue) {
                maxValue = value;
                maxString = str;
            }
        }
        return maxString;
    }

    private String generateDossierNo(String currentLatestDossierNo, String level1ItemCode) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
        String todayDate = today.format(formatter);
        if (currentLatestDossierNo == null || currentLatestDossierNo.isEmpty()) {
            return level1ItemCode.concat(StringPool.DASH).concat(todayDate).concat(StringPool.DASH).concat("0001");
        }
        String[] elements = currentLatestDossierNo.split(StringPool.DASH);
        String itemCode = elements[0];
        String date = elements[1];
        String index = elements[2];

        if (!date.equalsIgnoreCase(todayDate)) {
            return itemCode.concat(StringPool.DASH).concat(todayDate).concat(StringPool.DASH).concat("0001");
        }

        String newIndex = String.format("%04d", Integer.parseInt(index) + 1);
        return itemCode.concat(StringPool.DASH).concat(todayDate).concat(StringPool.DASH).concat(newIndex);

    }

    public static String getFirstItem(String input) {
        String[] items = input.split(",");
        return items[0];
    }

    private DictItem getLevel1Item(List<DictItem> dictItems) {
        return dictItems.stream().filter(d -> d.getLevel_() == 1).findFirst().orElse(null);
    }

    private List<FileStoreGovResponse> suggestFileStoreGovs(long employeeId, String level1ItemCode, String fromDate, String toDate, String keyword) {
        List<FileStoreGovResponse> suggestList = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DOSSIERNO, SERVICENAME, DEPARTMENTISSUE FROM (")
                .append(" SELECT DOSSIERNO,")
                .append(" ROW_NUMBER() OVER (PARTITION BY dossierNo ORDER BY f.createDate DESC) rn,")
                .append(" MAX(f.createDate) OVER (PARTITION BY dossierNo) maxCreateDate,")
                .append(" s.SERVICENAME, DEPARTMENTISSUE")
                .append(" FROM FILESTOREGOV f")
                .append(" INNER JOIN OPENCPS_SERVICEINFO s ON f.SERVICECODE = s.SERVICECODE")
                .append(" WHERE DOSSIERNO IS NOT NULL AND f.EMPLOYEEID = ?");

        List<Object> params = new ArrayList<>();
        params.add(employeeId);

        if (level1ItemCode != null && !level1ItemCode.isEmpty()) {
            sql.append(" AND DOSSIERNO LIKE ?");
            params.add("%" + level1ItemCode + "%");
        }

        // Thêm điều kiện tìm theo keyword cho 3 trường
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (");
            sql.append(" f.DOSSIERNO LIKE ? OR s.SERVICENAME LIKE ? OR f.DEPARTMENTISSUE LIKE ?");
            sql.append(" )");
            String likeKeyword = "%" + keyword + "%";
            params.add(likeKeyword);
            params.add(likeKeyword);
            params.add(likeKeyword);
        }

        if (fromDate != null && !fromDate.isEmpty() && toDate != null && !toDate.isEmpty()) {
            sql.append(" AND f.CREATEDATE BETWEEN TO_DATE(?, 'YYYY-MM-DD') AND TO_DATE(?, 'YYYY-MM-DD')");
            params.add(fromDate);
            params.add(toDate);
        }

        sql.append(" ) WHERE rn = 1 ORDER BY maxCreateDate DESC FETCH FIRST 20 ROWS ONLY");

        DataSource dataSource = InfrastructureUtil.getDataSource();
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement pst = connection.prepareStatement(sql.toString())
        ) {
            for (int i = 0; i < params.size(); i++) {
                pst.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    FileStoreGovResponse object = new FileStoreGovResponse();
                    object.setDossierNo(rs.getString("DOSSIERNO"));
                    object.setServiceName(rs.getString("SERVICENAME"));
                    object.setDepartmentIssue(rs.getString("DEPARTMENTISSUE"));
                    suggestList.add(object);
                }
            }
            return suggestList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suggestList;
    }
}
