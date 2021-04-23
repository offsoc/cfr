package org.benf.cfr.reader.bytecode.analysis.parse.statement;

import org.benf.cfr.reader.bytecode.analysis.loc.BytecodeLoc;
import org.benf.cfr.reader.bytecode.analysis.parse.Expression;
import org.benf.cfr.reader.bytecode.analysis.parse.LValue;
import org.benf.cfr.reader.bytecode.analysis.parse.Statement;
import org.benf.cfr.reader.bytecode.analysis.parse.rewriters.CloneHelper;
import org.benf.cfr.reader.bytecode.analysis.parse.rewriters.ExpressionRewriter;
import org.benf.cfr.reader.bytecode.analysis.parse.utils.*;
import org.benf.cfr.reader.bytecode.analysis.structured.StructuredStatement;
import org.benf.cfr.reader.util.ConfusedCFRException;
import org.benf.cfr.reader.util.collections.ListFactory;
import org.benf.cfr.reader.util.output.Dumper;

import java.util.List;

/**
 * This should not be used to aggregate statements, but only to produce statements when multiple statements
 * are generated by a single opcode.  (eg dup).
 */
public class CompoundStatement extends AbstractStatement {
    private List<Statement> statements;

    public CompoundStatement(BytecodeLoc loc, Statement... statements) {
        super(loc);
        this.statements = ListFactory.newImmutableList(statements);
    }

    @Override
    public BytecodeLoc getCombinedLoc() {
        return getLoc();
    }

    @Override
    public Dumper dump(Dumper dumper) {
        dumper.separator("{").newln();
        for (Statement statement : statements) {
            statement.dump(dumper);
        }
        dumper.separator("}").newln();
        return dumper;
    }

    @Override
    public void collectLValueAssignments(LValueAssignmentCollector<Statement> lValueAssigmentCollector) {
        throw new ConfusedCFRException("Should not be using compound statements here");
    }

    @Override
    public Statement deepClone(CloneHelper cloneHelper) {
        List<Statement> res = ListFactory.newList();
        for (Statement stm : statements) {
            res.add(stm.deepClone(cloneHelper));
        }
        return new CompoundStatement(getLoc(), res.toArray(new Statement[0]));
    }

    @Override
    public LValue getCreatedLValue() {
        throw new ConfusedCFRException("Should not be using compound statements here");
    }

    @Override
    public void collectLValueUsage(LValueUsageCollector lValueUsageCollector) {
        throw new ConfusedCFRException("Should not be using compound statements here");
    }

    @Override
    public Expression getRValue() {
        throw new ConfusedCFRException("Should not be using compound statements here");
    }

    @Override
    public void replaceSingleUsageLValues(LValueRewriter lValueRewriter, SSAIdentifiers ssaIdentifiers) {
        throw new ConfusedCFRException("Should not be using compound statements here");
    }

    @Override
    public void rewriteExpressions(ExpressionRewriter expressionRewriter, SSAIdentifiers ssaIdentifiers) {
        throw new ConfusedCFRException("Should not be using compound statements here");
    }

    @Override
    public boolean isCompound() {
        return true;
    }

    @Override
    public List<Statement> getCompoundParts() {
        return statements;
    }

    @Override
    public StructuredStatement getStructuredStatement() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean equivalentUnder(Object o, EquivalenceConstraint constraint) {
        if (o == null) return false;
        if (o == this) return true;
        if (getClass() != o.getClass()) return false;
        CompoundStatement other = (CompoundStatement) o;
        if (!constraint.equivalent(statements, other.statements)) return false;
        return true;
    }
}
