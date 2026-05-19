package com.insurance.reporting.batch;

import com.insurance.common.mapper.EntryTypeMapper;
import com.insurance.common.mapper.MethodCollMapper;
import com.insurance.common.util.AmountConverter;
import com.insurance.common.util.DateSanitizer;
import com.insurance.reporting.domain.AccountRecord;
import com.insurance.reporting.domain.AgentControl;
import com.insurance.reporting.dto.ReportLine;
import com.insurance.reporting.service.AgentLookupService;
import org.springframework.batch.item.ItemProcessor;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Implements the transformation from lines 55-140 of Reporting Mode.
 */
public class AccountTransformProcessor implements ItemProcessor<AccountRecord, ReportLine> {

    private final AgentLookupService agentLookupService;

    public AccountTransformProcessor(AgentLookupService agentLookupService) {
        this.agentLookupService = agentLookupService;
    }

    @Override
    public ReportLine process(AccountRecord item) {
        // Get agent info
        Optional<AgentControl> agentOpt = agentLookupService.lookupAgent(item.getBranch(), item.getAgent());
        Short inspectNo = agentOpt.map(AgentControl::getInspectNo).orElse(null);
        String agentName = agentOpt.map(AgentControl::getName).orElse("");

        // Map entry_type
        String type = null;
        if (item.getEntryType() != null && !item.getEntryType().isEmpty()) {
            type = EntryTypeMapper.map(item.getEntryType().charAt(0));
        }

        // Map method_coll
        String type2 = null;
        if (item.getMethodColl() != null && !item.getMethodColl().isEmpty()) {
            type2 = MethodCollMapper.map(item.getMethodColl().charAt(0));
        }

        // Convert amounts
        BigDecimal deb = AmountConverter.toCurrency(item.getDebCredAmt() != null ? item.getDebCredAmt() : 0);
        BigDecimal comm = AmountConverter.toCurrency(item.getCommAmount() != null ? item.getCommAmount() : 0);
        BigDecimal cash = AmountConverter.toCurrency(item.getCashAmt() != null ? item.getCashAmt() : 0);

        // Sanitize dates
        int entryDate = DateSanitizer.sanitize(item.getEntryDate() != null ? item.getEntryDate() : 0);
        int cashDate = DateSanitizer.sanitize(item.getCashDate() != null ? item.getCashDate() : 0);

        // Format with edit masks
        String emPrem = AmountConverter.formatWithEditMask(deb);
        String emComm = AmountConverter.formatWithEditMask(comm);
        String emCash = AmountConverter.formatWithEditMask(cash);

        return new ReportLine(
                item.getBranch(),
                item.getAgent(),
                item.getPolicy(),
                inspectNo,
                agentName,
                entryDate,
                cashDate,
                type,
                emPrem,
                emComm,
                emCash,
                type2
        );
    }
}
