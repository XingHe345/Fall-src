package net.minecraft.scoreboard;

public class ScoreObjective
{
    private final Scoreboard theScoreboard;
    private String name;

    /** The ScoreObjectiveCriteria for this objetive */
    private final IScoreObjectiveCriteria objectiveCriteria;
    private IScoreObjectiveCriteria.EnumRenderType renderType;
    private String displayName;

    public ScoreObjective(Scoreboard theScoreboardIn, String nameIn, IScoreObjectiveCriteria objectiveCriteriaIn)
    {
        this.theScoreboard = theScoreboardIn;
        this.name = nameIn;
        this.objectiveCriteria = objectiveCriteriaIn;
        this.displayName = nameIn;
        this.renderType = objectiveCriteriaIn.getRenderType();
    }

    public Scoreboard getScoreboard()
    {
        return this.theScoreboard;
    }

    public String getName()
    {
        name = name.replace("\uD83C\uDF89", "");
        name = name.replace("\uD83C\uDF81", "");
        name = name.replace("\uD83D\uDC79", "");
        name = name.replace("\uD83C\uDFC0", "");
        name = name.replace("⚽", "");
        name = name.replace("\uD83C\uDF6D", "");
        name = name.replace("\uD83C\uDF20", "");
        name = name.replace("\uD83D\uDC7E", "");
        name = name.replace("\uD83D\uDC0D", "");
        name = name.replace("\uD83D\uDD2E", "");
        name = name.replace("\uD83D\uDC7D", "");
        name = name.replace("\uD83D\uDCA3", "");
        name = name.replace("\uD83C\uDF6B", "");
        name = name.replace("\uD83C\uDF82", "");
        return this.name;
    }

    public IScoreObjectiveCriteria getCriteria()
    {
        return this.objectiveCriteria;
    }

    public String getDisplayName()
    {
        displayName = displayName.replace("\uD83C\uDF89", "");
        displayName = displayName.replace("\uD83C\uDF81", "");
        displayName = displayName.replace("\uD83D\uDC79", "");
        displayName = displayName.replace("\uD83C\uDFC0", "");
        displayName = displayName.replace("⚽", "");
        displayName = displayName.replace("\uD83C\uDF6D", "");
        displayName = displayName.replace("\uD83C\uDF20", "");
        displayName = displayName.replace("\uD83D\uDC7E", "");
        displayName = displayName.replace("\uD83D\uDC0D", "");
        displayName = displayName.replace("\uD83D\uDD2E", "");
        displayName = displayName.replace("\uD83D\uDC7D", "");
        displayName = displayName.replace("\uD83D\uDCA3", "");
        displayName = displayName.replace("\uD83C\uDF6B", "");
        displayName = displayName.replace("\uD83C\uDF82", "");
        return this.displayName;
    }

    public void setDisplayName(String nameIn)
    {
        this.displayName = nameIn;
        this.theScoreboard.onObjectiveDisplayNameChanged(this);
    }

    public IScoreObjectiveCriteria.EnumRenderType getRenderType()
    {
        return this.renderType;
    }

    public void setRenderType(IScoreObjectiveCriteria.EnumRenderType type)
    {
        this.renderType = type;
        this.theScoreboard.onObjectiveDisplayNameChanged(this);
    }
}
