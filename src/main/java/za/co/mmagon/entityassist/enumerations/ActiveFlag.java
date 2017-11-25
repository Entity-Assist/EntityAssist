package za.co.mmagon.entityassist.enumerations;

import javax.validation.constraints.NotNull;
import java.util.EnumSet;
import java.util.Set;

/**
 * Active Range ordered from Unknown -> Removed -> Archived/Visible -> Active -> Highlighted -> Permanent
 * <p>
 * Meant as text with indexer for support on Gen 3 and Gen 4 databases
 */
public enum ActiveFlag
{
	/**
	 * UnknownRange
	 */
	Unknown,
	Unspecified,
	/**
	 * RemovedRange
	 **/
	Deleted,
	Hidden,
	Invisible,
	/**
	 * VisibleRange
	 */
	Archived,
	LongTermStorage,
	MidTermStorage,
	ShortTermStorage,
	/**
	 * ActiveRange
	 */
	Active,
	Current,
	/**
	 * HighlightedRange
	 **/
	Important,
	Highlighted,
	/**
	 * PermanentRange
	 */
	Always,
	Permanent;

	private static final Set<ActiveFlag> PermanentRange = EnumSet.of(Always, Permanent);

	private static final Set<ActiveFlag> HighlightedRangeAndUp = EnumSet.of(Important, Highlighted, Always, Permanent);
	private static final Set<ActiveFlag> HighlightedRange = EnumSet.of(Important, Highlighted);

	private static final Set<ActiveFlag> ActiveRangeAndUp = EnumSet.of(Active, Current, Important, Highlighted, Always, Permanent);
	private static final Set<ActiveFlag> ActiveRange = EnumSet.of(Active, Current);

	private static final Set<ActiveFlag> VisibleRangeAndUp = EnumSet.of(Archived, LongTermStorage, MidTermStorage, ShortTermStorage, Active, Current, Important, Highlighted, Always, Permanent);
	private static final Set<ActiveFlag> VisibleRange = EnumSet.of(Archived, LongTermStorage, MidTermStorage, ShortTermStorage);

	private static final Set<ActiveFlag> RemovedRangeAndUp = EnumSet.of(Deleted, Hidden, Invisible, Archived, LongTermStorage, MidTermStorage, ShortTermStorage, Active, Current, Important, Highlighted, Always, Permanent);
	private static final Set<ActiveFlag> RemovedRange = EnumSet.of(Deleted, Hidden, Invisible);

	/**
	 * Returns the permanent range of values
	 *
	 * @return
	 */
	@NotNull
	public static Set<ActiveFlag> getPermanentRange()
	{
		return PermanentRange;
	}

	/**
	 * Returns the highlighted and up range of values
	 *
	 * @return
	 */
	@NotNull
	public static Set<ActiveFlag> getHighlightedRangeAndUp()
	{
		return HighlightedRangeAndUp;
	}

	/**
	 * Returns the highlighted range of visible values
	 *
	 * @return
	 */
	@NotNull
	public static Set<ActiveFlag> getHighlightedRange()
	{
		return HighlightedRange;
	}

	/**
	 * Returns the active range and up values
	 *
	 * @return
	 */
	@NotNull
	public static Set<ActiveFlag> getActiveRangeAndUp()
	{
		return ActiveRangeAndUp;
	}

	/**
	 * Returns the active range only
	 *
	 * @return
	 */
	@NotNull
	public static Set<ActiveFlag> getActiveRange()
	{
		return ActiveRange;
	}

	/**
	 * Returns the visible range and up values
	 *
	 * @return
	 */
	@NotNull
	public static Set<ActiveFlag> getVisibleRangeAndUp()
	{
		return VisibleRangeAndUp;
	}

	/**
	 * Returns the visible range
	 *
	 * @return
	 */
	@NotNull
	public static Set<ActiveFlag> getVisibleRange()
	{
		return VisibleRange;
	}

	/**
	 * Returns the removed range and up values
	 *
	 * @return
	 */
	@NotNull
	public static Set<ActiveFlag> getRemovedRangeAndUp()
	{
		return RemovedRangeAndUp;
	}

	/**
	 * Returns the removed range
	 *
	 * @return
	 */
	@NotNull
	public static Set<ActiveFlag> getRemovedRange()
	{
		return RemovedRange;
	}
}
