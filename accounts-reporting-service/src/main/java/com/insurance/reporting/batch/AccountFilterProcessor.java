package com.insurance.reporting.batch;

import com.insurance.reporting.domain.AccountRecord;
import com.insurance.reporting.domain.AgentControl;
import com.insurance.reporting.service.AgentLookupService;
import org.springframework.batch.item.ItemProcessor;

import java.util.Optional;

/**
 * Implements the filtering chain from lines 34-52 of Reporting Mode.
 * Returns null for filtered records (Spring Batch skips null items).
 */
public class AccountFilterProcessor implements ItemProcessor<AccountRecord, AccountRecord> {

    private final AgentLookupService agentLookupService;

    public AccountFilterProcessor(AgentLookupService agentLookupService) {
        this.agentLookupService = agentLookupService;
    }

    @Override
    public AccountRecord process(AccountRecord item) {
        // 1. Build agent key and lookup agent, check status='A' (lines 34-44)
        Optional<AgentControl> agentOpt = agentLookupService.lookupAgent(item.getBranch(), item.getAgent());
        if (agentOpt.isEmpty() || !"A".equals(agentOpt.get().getStatus())) {
            return null;
        }

        // 2. Check entry_date < 20131199 (line 47)
        if (item.getEntryDate() == null || item.getEntryDate() >= 20131199) {
            return null;
        }

        // 3. Check method_coll first char NOT IN ('R','X','S') (lines 48-49)
        if (item.getMethodColl() != null && !item.getMethodColl().isEmpty()) {
            char mc1 = item.getMethodColl().charAt(0);
            if (mc1 == 'R' || mc1 == 'X' || mc1 == 'S') {
                return null;
            }
        }

        // 4. Check entry_type != 'R' (line 50)
        if ("R".equals(item.getEntryType())) {
            return null;
        }

        return item;
    }
}
