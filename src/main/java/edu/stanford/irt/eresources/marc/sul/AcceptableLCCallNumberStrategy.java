package edu.stanford.irt.eresources.marc.sul;

import java.util.List;
import java.util.Set;

import edu.stanford.irt.eresources.TextParserHelper;
import edu.stanford.irt.eresources.marc.MARCRecordSupport;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;
import edu.stanford.lane.lcsh.LcshMapManager;

public class AcceptableLCCallNumberStrategy implements InclusionStrategy {

    private LcshMapManager lcshMapManager = new LcshMapManager();

    List<String> acceptableLCCallNumberPrefixes;

    public AcceptableLCCallNumberStrategy(final List<String> acceptableLCCallNumberPrefixes) {
        this.acceptableLCCallNumberPrefixes = acceptableLCCallNumberPrefixes;
    }

    @Override
    public boolean isAcceptable(final Record marcRecord) {
        Set<String> cns = MARCRecordSupport.extractLCCallNumbers(marcRecord);
        if (includedInAcceptableLCCallNumberPrefixes(cns)) {
            return true;
        }
        // augment callnumber list with mapped LCSH->callnumber values
        // but skip cn mapping for fiction records
        if (isFiction(marcRecord)) {
            return false;
        }
        cns.clear();
        MARCRecordSupport.getFields(marcRecord, "650").filter((final Field f) -> ("07".indexOf(f.getIndicator2()) > -1))
                .forEach((final Field f) -> {
                    StringBuilder sb = new StringBuilder();
                    f.getSubfields().stream().filter((final Subfield sf) -> "ax".indexOf(sf.getCode()) > -1)
                            .forEach((final Subfield sf) -> {
                                if ('x' == sf.getCode()) {
                                    sb.append("--");
                                }
                                sb.append(TextParserHelper.maybeStripTrailingPeriod(sf.getData()));
                            });
                    cns.addAll(this.lcshMapManager.getCallnumbersForHeading(sb.toString()));
                });
        return includedInAcceptableLCCallNumberPrefixes(cns);
    }

    private boolean includedInAcceptableLCCallNumberPrefixes(final Set<String> callnumbers) {
        for (String cn : callnumbers) {
            for (String lccn : this.acceptableLCCallNumberPrefixes) {
                if (cn.startsWith(lccn)) {
                    return true;
                }
            }
        }
        return false;
    }
}
