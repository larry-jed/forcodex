package com.example.mcpserver.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AiSqlService {

    private static final String PROMPT_TEMPLATE = String.join("\n",
            "You are a SQL generator for a PostgreSQL database.",
            "Available tables (column names are snake_case):",
            "- ed_cdi_contract(contract_order_number, file_number, contract_name, contract_party_a, contract_party_b, contact_department_party_b,",
            "  contact_person_party_b, contact_person_party_a, phone_contact_person_party_a, province, region, operator, professional_directory_level1,",
            "  professional_directory_level2, professional_directory_level3, professional_directory_level4, is_framework, is_framework_subitem,",
            "  corresponding_framework_contract, contract_amount_cny, signing_date, invoice_file_number, order_classification, remark, affiliated_company,",
            "  corresponding_approval_order_number, corresponding_acceptance_certificate_number)",
            "- person_resume(name, mobile, id_card, gender, id_validity_end, degree, second_major, first_major, ctc_validity, remark, login_name, contract_type)",
            "- person_certificate(name, id_card, cert_kind, cert_specific_kind, cert_specific_tag, cert_specific_msg, get_date, out_date, cert_no,",
            "  get_nuit, remark, login_name)",
            "- enterprise_certificate(certificate_name, certificate_content, certificate_number, level, issuing_authority, certificate_issue_date,",
            "  reexamination_date, validity_date, remark)",
            "Rules:",
            "1. Always convert CamelCase field names in the user question to the snake_case column names shown above.",
            "2. Return exactly one safe SELECT statement. Do not include explanations or comments.",
            "3. Prefer filtering with WHERE clauses inferred from the question and add LIMIT clauses when unspecified (use LIMIT 100).",
            "4. Never modify the schema and do not perform INSERT/UPDATE/DELETE.",
            "Question: %s");

    private static final String CONTRACT_DEFAULT_SQL = "SELECT contract_order_number, contract_name, contract_party_a, contract_party_b, " +
            "contract_amount_cny, signing_date, province, region FROM ed_cdi_contract ORDER BY signing_date DESC NULLS LAST LIMIT 100";

    private static final String PERSON_RESUME_DEFAULT_SQL = "SELECT name, mobile, id_card, gender, id_validity_end, degree, second_major, first_major, " +
            "ctc_validity, contract_type FROM person_resume ORDER BY name ASC LIMIT 100";

    private static final String PERSON_CERTIFICATE_DEFAULT_SQL = "SELECT name, id_card, cert_kind, cert_specific_kind, cert_specific_tag, cert_specific_msg, " +
            "get_date, out_date, cert_no, get_nuit FROM person_certificate ORDER BY out_date NULLS FIRST LIMIT 100";

    private static final String ENTERPRISE_CERTIFICATE_DEFAULT_SQL = "SELECT certificate_name, certificate_content, certificate_number, level, issuing_authority, " +
            "certificate_issue_date, reexamination_date, validity_date FROM enterprise_certificate ORDER BY validity_date DESC NULLS LAST LIMIT 100";

    private final ObjectProvider<ChatClient> chatClientProvider;

    public AiSqlService(ObjectProvider<ChatClient> chatClientProvider) {
        this.chatClientProvider = chatClientProvider;
    }

    public Mono<String> generateSql(String question) {
        return Mono.fromCallable(() -> {
            ChatClient chatClient = chatClientProvider.getIfAvailable();
            if (chatClient != null) {
                String prompt = String.format(PROMPT_TEMPLATE, question);
                return chatClient.call(prompt);
            }
            return buildFallbackSql(question);
        }).map(String::trim);
    }

    private String buildFallbackSql(String question) {
        String sanitized = question == null ? "" : question.toLowerCase();

        if (containsAny(sanitized, "企业资质", "enterprise qualification", "enterprise certificate", "company certificate", "企业证书")) {
            return ENTERPRISE_CERTIFICATE_DEFAULT_SQL;
        }

        if (containsAny(sanitized, "证书", "certificate")) {
            return PERSON_CERTIFICATE_DEFAULT_SQL;
        }

        if (containsAny(sanitized, "人员", "简历", "personnel", "resume", "员工")) {
            return PERSON_RESUME_DEFAULT_SQL;
        }

        return CONTRACT_DEFAULT_SQL;
    }

    private boolean containsAny(String text, String... tokens) {
        for (String token : tokens) {
            if (text.contains(token)) {
                return true;
            }
        }
        return false;
    }
}
