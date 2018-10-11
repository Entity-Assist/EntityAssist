package com.jwebmp.entityassist.enumerations;

import javax.validation.constraints.NotNull;
import java.util.EnumSet;
import java.util.Set;

/**
 * Active Range ordered from Unknown to Removed to Archived/Visible to Active to Highlighted to Permanent
 * <p>
 * Meant as text with indexer for support on Gen 3 and Gen 4 databases
 */
public enum ActiveFlag
{
	/**
	 * UnknownRange
	 */
	Unknown,
	/**
	 * If the item is unspecified
	 */
	Unspecified,
	/**
	 * RemovedRange
	 **/
	Deleted,
	/**
	 * If the item is simply hidden
	 */
	Hidden,
	/**
	 * If the item should be invisible
	 */
	Invisible,
	/**
	 * If the active status is errored
	 */
	Errored,
	/**
	 * VisibleRange
	 */
	Archived,
	/**
	 * If the item is saved for long term storage
	 */
	LongTermStorage,
	/**
	 * If the item is saved for mid term storage
	 */
	MidTermStorage,
	/**
	 * If the item is saved for short term storage
	 */
	ShortTermStorage,
	/**
	 * If the item is resolved
	 */
	Resolved,
	/**
	 * If the item is completed
	 */
	Completed,
	/**
	 * ActiveRange
	 */
	Active,
	/**
	 * If the item is current
	 */
	Current,
	/**
	 * HighlightedRange
	 **/
	Important,
	/**
	 * If the item is highlighted
	 */
	Highlighted,
	/**
	 * PermanentRange
	 */
	Always,
	/**
	 * If the item is permanent
	 */
	Permanent;

	private static final Set<ActiveFlag> PermanentRange = EnumSet.of(Always, Permanent);

	private static final Set<ActiveFlag> HighlightedRangeAndUp = EnumSet.of(Important, Highlighted, Always, Permanent);
	private static final Set<ActiveFlag> HighlightedRange = EnumSet.of(Important, Highlighted);

	private static final Set<ActiveFlag> ActiveRangeAndUp = EnumSet.of(Active, Current, Important, Highlighted, Always, Permanent);
	private static final Set<ActiveFlag> ActiveRange = EnumSet.of(Active, Current);

	private static final Set<ActiveFlag> VisibleRangeAndUp = EnumSet.of(Archived, LongTermStorage, MidTermStorage, ShortTermStorage, Resolved, Completed, Active, Current,
	                                                                    Important,
	                                                                    Highlighted, Always, Permanent);
	private static final Set<ActiveFlag> VisibleRange = EnumSet.of(Archived, LongTermStorage, MidTermStorage, ShortTermStorage, Resolved, Completed);

	private static final Set<ActiveFlag> RemovedRangeAndUp = EnumSet.of(Deleted, Hidden, Invisible, Archived, LongTermStorage, MidTermStorage, ShortTermStorage, Resolved,
	                                                                    Completed, Errored,
	                                                                    Active, Current, Important, Highlighted, Always, Permanent);
	private static final Set<ActiveFlag> RemovedRange = EnumSet.of(Deleted, Hidden, Invisible, Errored);

	/**
	 * Returns the permanent range of values
	 *
	 * @return ActiveFlags
	 */
	@NotNull
	public static Set<ActiveFlag> getPermanentRange()
	{
		return PermanentRange;
	}

	/**
	 * Returns the highlighted and up range of values
	 *
	 * @return ActiveFlags
	 */
	@NotNull
	public static Set<ActiveFlag> getHighlightedRangeAndUp()
	{
		return HighlightedRangeAndUp;
	}

	/**
	 * Returns the highlighted range of visible values
	 *
	 * @return ActiveFlags
	 */
	@NotNull
	public static Set<ActiveFlag> getHighlightedRange()
	{
		return HighlightedRange;
	}

	/**
	 * Returns the active range and up values
	 *
	 * @return ActiveFlags
	 */
	@NotNull
	public static Set<ActiveFlag> getActiveRangeAndUp()
	{
		return ActiveRangeAndUp;
	}

	/**
	 * Returns the active range only
	 *
	 * @return ActiveFlags
	 */
	@NotNull
	public static Set<ActiveFlag> getActiveRange()
	{
		return ActiveRange;
	}

	/**
	 * Returns the visible range and up values
	 *
	 * @return ActiveFlags
	 */
	@NotNull
	public static Set<ActiveFlag> getVisibleRangeAndUp()
	{
		return VisibleRangeAndUp;
	}

	/**
	 * Returns the visible range
	 *
	 * @return ActiveFlags
	 */
	@NotNull
	public static Set<ActiveFlag> getVisibleRange()
	{
		return VisibleRange;
	}

	/**
	 * Returns the removed range and up values
	 *
	 * @return ActiveFlags
	 */
	@NotNull
	public static Set<ActiveFlag> getRemovedRangeAndUp()
	{
		return RemovedRangeAndUp;
	}

	/**
	 * Returns the removed range
	 *
	 * @return ActiveFlags
	 */
	@NotNull
	public static Set<ActiveFlag> getRemovedRange()
	{
		return RemovedRange;
	}
}
