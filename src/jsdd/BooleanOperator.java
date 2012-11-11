package jsdd;

/**
 * Interface for any binary boolean operator over SDDs.
 * 
 * @author Ricardo Herrmann
 */
public interface BooleanOperator {

	SDD apply(ConstantSDD s1, ConstantSDD s2);

	SDD apply(ConstantSDD s1, LiteralSDD s2);

	SDD apply(LiteralSDD s1, ConstantSDD s2);

}
